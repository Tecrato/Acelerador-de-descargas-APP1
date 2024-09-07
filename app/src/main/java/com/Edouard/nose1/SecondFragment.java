package com.Edouard.nose1;

import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.Edouard.nose1.databinding.FragmentSecondBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;



public class SecondFragment extends Fragment {
    private FragmentSecondBinding binding;
    public Map<String, List<String>> get(String urlParam, boolean headers) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlParam).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            return urlConnection.getHeaderFields();

        } catch (MalformedURLException e) {
            Log.e("NetworkUtil", "Invalid URL: " + e.getMessage());
        } catch (IOException e) {
            Log.e("NetworkUtil", "Network error: " + e.getMessage());
        }
        return null;
    }

    public JSONObject get(String urlParam){
        try {
            URL url = new URL(urlParam);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            try {
                urlConnection.getHeaderFields();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject jsonResponse = new JSONObject(response.toString());

                return jsonResponse;
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Maneja la excepción
        }
        return null;
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.btnMatch.setOnClickListener(v -> {
            binding.btnSend.setEnabled(false);
            binding.progressBar.setProgress(50,true);
            binding.progressBar.setVisibility(View.VISIBLE);
            new Thread(() -> {
                String texto = binding.inputUrl.getText().toString();

                boolean name = false;
                String name_text = "";

                Map<String, List<String>> result =  get(texto, true);
                if (result == null) {
                    return;
                }
                for (Map.Entry<String, List<String>> entry : result.entrySet()) {
                    Log.i("El header",entry.getKey() + ": " + entry.getValue());
                    if (entry.getKey() != null || Objects.equals(entry.getKey(), "content-disposition")) {
                        name_text = entry.getValue().toString();
                        String[] a = name_text.split(";");
                        for (String b : a) {
                            if (b.trim().startsWith("filename=")){
                                name_text = b.replace("filename=","").trim();
                                name = true;
                                break;
                            }
                        }
                        break;
                    }
                }
                boolean finalName = name;
                String finalName_text = name_text;
                getActivity().runOnUiThread(() -> {
                    if (finalName) {
                        binding.inputNombre.setText(finalName_text);
                        binding.btnSend.setEnabled(true);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        String[] a = texto.split("/");
                        String b =  a[a.length-1];
                        Log.i("mira chamo",b);
                        binding.inputNombre.setText(b);
                        binding.btnSend.setEnabled(true);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }).start();

        });
        binding.btnSend.setOnClickListener(v-> {
            new Thread(() -> {
                if (binding.inputUrl.getText().toString().isEmpty()) {
                    Snackbar.make(view,"Ingrese una url", 2000)
                            .setAnchorView(R.id.fab).show();
                    return;
                }
                else if (binding.inputNombre.getText().toString().isEmpty()) {
                    Snackbar.make(view,"especifique un nombre por favor", 2000)
                            .setAnchorView(R.id.fab).show();
                    return;
                }
                get(Cosas.url_program+"descargas/add_web?url=" + binding.inputUrl.getText().toString()+"&nombre="+binding.inputNombre.getText().toString().trim());
            }).start();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}