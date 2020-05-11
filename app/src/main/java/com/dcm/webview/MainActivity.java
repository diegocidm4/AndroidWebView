package com.dcm.webview;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    private WebView webView=null;
    private EditText etUrl=null;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    private ValueCallback<Uri[]> mFilePathCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView)findViewById(R.id.mainwebview);
        etUrl = (EditText)findViewById(R.id.etUrl);
        Button bIr = (Button)findViewById(R.id.ir);

        //No funciona descarga
//        etUrl.setText("https://www.flaticon.es/icono-gratis/amor_2913124");
//        etUrl.setText("http://sedeapl.dgt.gob.es/WEB_IEST_CONSULTA/configurarInformePredefinido.faces");
        etUrl.setText("https://www.ine.es/jaxiT3/Tabla.htm?t=31304");
        //Funciona descarga
//        etUrl.setText("https://www.registradores.org/documentacion-y-descargas/manuales-y-descargas");
        bIr.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                cargaContenidoWeb(etUrl.getText().toString());
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cargaContenidoWeb(String url)
    {
        webView.setVisibility(WebView.VISIBLE);
        webView.setInitialScale(1);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setDownloadListener(new DownloadListener()
        {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                //Recuperamos los datos del fichero a descargar
                FicheroDescarga ficheroDescarga = DownloadUtils.recuperaDatosFichero(contentDisposition);
                //Si se intenta descargar un fichero, lo descargamos
                DownloadManager.Request request = DownloadUtils.descargaDocumento(url, userAgent, contentDisposition, mimeType, ficheroDescarga, MainActivity.this);
//                request = request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

                final DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                final long downloadID = dm.enqueue(request);
                final String nombreFicheroFinal = ficheroDescarga.getNombre();
                final String extensionFinal = ficheroDescarga.getExtension();

                BroadcastReceiver onComplete = new BroadcastReceiver() {

                    public void onReceive(Context ctxt, Intent intent) {
//                        if (dm != null) {
//                            //Guardamos el documento descargado en la aplicación
//                            DownloadUtils.guardaDocumentoDescargado(dm, downloadID, nombreFicheroFinal, extensionFinal, getApplicationContext());
//                        }
                        unregisterReceiver(this);
                    }
                };

                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {

            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Selección de documento a anexar"), INPUT_FILE_REQUEST_CODE);

                return true;
            }
        });

        webView.loadUrl(url);
    }
}
