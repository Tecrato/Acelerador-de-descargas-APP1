package com.Edouard.nose1;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.appcompat.app.AppCompatActivity;

import com.Edouard.nose1.databinding.FragmentFirstBinding;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FirstFragment extends Fragment {
    private MediaPlayer mp;

    private FragmentFirstBinding binding;
    private String url_program = "http://192.168.1.110:5000/";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public JSONObject get(String urlParam) {

        try {
            URL url = new URL(urlParam);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            try {
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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mp = MediaPlayer.create(getContext(), R.raw.ramas1);
        mp.setVolume(.8f,.8f);

        binding.ButtonAbrirPrograma.setOnClickListener(v -> {
            new Thread(() -> {
                get(url_program+"open_program");
            }).start();
        });

        binding.buttonSonido1.setOnClickListener(v ->{
            new Thread(() -> {
                get(url_program+"api_close");
            }).start();
//            NavHostFragment.findNavController(FirstFragment.this)
//                    .navigate(R.id.first_to_settings);
//            if (mp != null && mp.isPlaying()) {
//                mp.pause();
//                mp.seekTo(0);
//            } else {
//                mp.start();
//            }
        }
        );
        binding.buttonIpChange.setOnClickListener(v -> {
            String string = binding.inputIp.getText().toString();
            if (!string.startsWith("http://")){
                string = "http://"+string;
            }
            if (!string.endsWith("/")){
                string = string+"/";
            }

            url_program = string;

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mp.release();
        binding = null;
    }

}