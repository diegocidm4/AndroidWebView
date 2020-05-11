package com.dcm.webview;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Diego on 08/11/2018.
 */
public class DownloadUtils {

    public static FicheroDescarga recuperaDatosFichero(String contentDisposition)
    {
        FicheroDescarga descarga = new FicheroDescarga();
        String filename = "filename=";
        int pos = contentDisposition.indexOf(filename);
        String nombreFichero = "descarga";
        String extension = "pdf";
        String descripcion = "Descarga documento";
        if (pos > 0) {
            int posPunto = contentDisposition.lastIndexOf(".");
            if(posPunto>0) {
                nombreFichero = contentDisposition.substring(pos + filename.length(), posPunto);
                nombreFichero = nombreFichero.replaceAll("\"", "");
                extension = contentDisposition.substring(posPunto + 1);
                extension = extension.replaceAll("\"", "");
            }
            descripcion = contentDisposition.substring(pos);
            descripcion = descripcion.replaceAll("\"", "");
        }

        descarga.setNombre(nombreFichero);
        descarga.setExtension(extension);
        descarga.setDescripcion(descripcion);

        return descarga;
    }

    public static DownloadManager.Request descargaDocumento (String url, String userAgent,
                                                             String contentDisposition, String mimeType,
                                                             FicheroDescarga fichero, Context context)
    {
        //Descarga del documento utilizando DownloadManager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimeType);
        String cookies = CookieManager.getInstance().getCookie(url);
//        String cookies = SessionUtils.recuperaCookiesToString();
        request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Downloading file...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                mimeType));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        return request;

    }

//    public static void guardaDocumentoDescargado(DownloadManager dm, long downloadID, String nombreFicheroFinal, String extensionFinal, Context myContext)
//    {
//        try {
//            ParcelFileDescriptor file = dm.openDownloadedFile(downloadID);
//            InputStream fileStream = new FileInputStream(file.getFileDescriptor());
//            File dest = FileUtils.writeDownloadFile(nombreFicheroFinal, extensionFinal, fileStream);
//
//            Files files = new Files(myContext);
//            files.moveAndReturnFile(dest, nombreFicheroFinal, extensionFinal);
//
//            //Borramos el fichero descargado
//            dest.delete();
//
//            Toast.makeText(myContext, "Fichero descargado correctamente", Toast.LENGTH_LONG).show();
//
//        } catch (Exception ex) {
//            Toast.makeText(myContext, "Error al descargar el fichero", Toast.LENGTH_LONG).show();
//        }
//
//    }
}
