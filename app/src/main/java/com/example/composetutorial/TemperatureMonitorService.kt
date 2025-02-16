package com.example.composetutorial

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

private var lastNotifiedTemp: Float? = null
private var isFirstReading = true
private const val TEMP_THRESHOLD = 0.5f

class TemperatureMonitorService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var temperatureSensor: Sensor? = null
    private val serviceNotificationId = 1
    private val channelId = "temperature_notification_channel"


    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Temperature Monitoring")
            .setContentText("Monitoring ambient temperature")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setColor(ContextCompat.getColor(this, R.color.black))
            .build()

        startForeground(serviceNotificationId, notification)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        temperatureSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            val temperature = event.values[0]
            //Log.d("Temperature Monitor", "Temp = $temperature°C")

            //Preventing the temperature change notification at the beginning
            if (isFirstReading) {
                isFirstReading = false
                lastNotifiedTemp = temperature
                return
            }

            if (lastNotifiedTemp == null || kotlin.math.abs(temperature - lastNotifiedTemp!!) >= TEMP_THRESHOLD) {
                sendTemperatureNotification(temperature)
                lastNotifiedTemp = temperature
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { //Empty
    }

    private fun sendTemperatureNotification(temperature: Float) {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Temperature Notification")
            .setContentText("Temperature changed. Now: $temperature °C")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setColor(ContextCompat.getColor(this, R.color.black))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(2, notification)


    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Temperature Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}