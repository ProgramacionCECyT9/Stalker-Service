package mx.ipn.edu.practica25_servicios;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServicioMusica extends Service {
    MediaPlayer reproductor;
    private NotificationManager nm;
    private static final int ID_NOTIFICACION_CREAR = 1;
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Override
    public void onCreate() {
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Toast.makeText(this, "Servicio creado",
                Toast.LENGTH_SHORT).show();
    }

//    START_STICKY: Cuando sea posible el sistema tratar� de recrear el servicio, se realizar� una llamada a onStartCommand() pero con el par�metro intencion igual a null. Esto tiene sentido cuando el servicio puede arrancar sin informaci�n adicional, como por ejemplo, el servicio mostrado para la reproducci�n de m�sica de fondo.
//    START_NOT_STICKY: El sistema no tratar� de volver a crear el servicio, por lo tanto el par�metro intencion nunca podr� ser igual a null. Esto tiene sentido cuando el servicio no puede reanudarse una vez interrumpido.
//    START_REDELIVER_INTENT: El sistema tratar� de volver a crear el servicio. El par�metro intencion ser� el que se utiliz� en la �ltima llamada startService(Intent).

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        Bundle bundle = intenc.getExtras();
        final Double latitude = bundle.getDouble("latitude");
        final Double longitude = bundle.getDouble("longitude");

        Toast.makeText(this,"Servicio arrancado "+ idArranque,
                Toast.LENGTH_SHORT).show();

        Notification notificacion = new Notification(
                R.mipmap.ic_launcher,
                "Creando Servicio de M�sica",
                System.currentTimeMillis() );

        PendingIntent intencionPendiente = PendingIntent.getActivity(
                this, 0, new Intent(this, MainActivity.class), 0);
        notificacion.setLatestEventInfo(this, "Enviando Correo",
                "informaci�n adicional", intencionPendiente);


        Runnable helloRunnable = new Runnable() {
            public void run() {
                String mail = "laralacesar@gmail.com";
                String subject = "We love Pertica";
                String message = "Latutude: " + String.valueOf(latitude) +
                        "Longitude: " + String.valueOf(longitude);
                SendEmail(mail, subject, message);
            }
        };

        executor.scheduleAtFixedRate(helloRunnable, 0, 10, TimeUnit.SECONDS);
        nm.notify(ID_NOTIFICACION_CREAR, notificacion);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        executor.shutdownNow();
        nm.cancel(ID_NOTIFICACION_CREAR);
        Toast.makeText(this,"Servicio detenido",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

    public void SendEmail(String mail, String subject, String message){
        Log.d("Send email", mail);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(emailIntent);
    }
}
