package com.nexaria.volumetv;

import android.app.Activity;
import android.media.AudioManager;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private LoudnessEnhancer enhancer;
    private TextView status, amount;
    private SeekBar seek;
    private int current = 0;
    private final int bg = Color.rgb(10, 15, 30);
    private final int cyan = Color.rgb(45, 220, 235);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        buildUi();
        initEffect();
    }

    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }

    private TextView text(String s, int size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(Color.WHITE); t.setTextSize(size);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(28), dp(22), dp(28), dp(22));
        root.setBackgroundColor(bg);

        TextView brand = text("NEXA", 34, true); brand.setTextColor(cyan);
        root.addView(brand, new LinearLayout.LayoutParams(-1, -2));
        TextView title = text("VOLUME TV", 19, true);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-1, -2); tp.bottomMargin = dp(8);
        root.addView(title, tp);
        TextView note = text("Control de refuerzo de audio", 15, false);
        root.addView(note, new LinearLayout.LayoutParams(-1, -2));

        amount = text("Refuerzo: 0%", 25, true);
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1, -2); ap.topMargin = dp(26); ap.bottomMargin = dp(8);
        root.addView(amount, ap);
        seek = new SeekBar(this); seek.setMax(20); seek.setProgress(0);
        seek.setContentDescription("Nivel de refuerzo de audio");
        root.addView(seek, new LinearLayout.LayoutParams(-1, -2));
        TextView scale = text("0%     ·     25%     ·     50%     ·     75%     ·     100%", 12, false);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, -2); sp.topMargin = dp(4);
        root.addView(scale, sp);

        Button apply = new Button(this); apply.setText("ACTIVAR REFUERZO");
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2); bp.topMargin = dp(20);
        root.addView(apply, bp);
        Button reset = new Button(this); reset.setText("RESTABLECER");
        root.addView(reset, new LinearLayout.LayoutParams(-1, -2));

        status = text("Preparando motor de audio…", 13, false);
        LinearLayout.LayoutParams stp = new LinearLayout.LayoutParams(-1, -2); stp.topMargin = dp(14);
        root.addView(status, stp);
        TextView footer = text("NEXA · Audio tools", 11, false);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(-1, -2); fp.topMargin = dp(18);
        root.addView(footer, fp);
        setContentView(root);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b, int p, boolean user) { current = p; amount.setText("Refuerzo: " + (p * 5) + "%"); }
            public void onStartTrackingTouch(SeekBar b) {}
            public void onStopTrackingTouch(SeekBar b) { applyGain(); }
        });
        apply.setOnClickListener(v -> applyGain());
        reset.setOnClickListener(v -> { seek.setProgress(0); applyGain(); });
    }

    private void initEffect() {
        try {
            enhancer = new LoudnessEnhancer(0);
            enhancer.setEnabled(false);
            status.setText("Motor de audio disponible. Ajusta el nivel y activa.");
        } catch (Exception | LinkageError e) {
            enhancer = null;
            status.setText("Este proyector no permite el refuerzo global desde apps normales.");
        }
    }

    private void applyGain() {
        if (enhancer == null) { status.setText("El sistema no admite este efecto global de audio."); return; }
        try {
            if (current == 0) {
                enhancer.setEnabled(false);
                status.setText("Refuerzo desactivado.");
            } else {
                // 0..20 steps => 0..1000 millibels (up to +10 dB).
                enhancer.setTargetGain(current * 50);
                enhancer.setEnabled(true);
                status.setText("Refuerzo solicitado: +" + (current * 5) + "%. El efecto depende del sistema.");
            }
        } catch (Exception | LinkageError e) {
            status.setText("Android rechazó el efecto de audio en este dispositivo.");
        }
    }

    @Override protected void onDestroy() {
        if (enhancer != null) {
            try { enhancer.setEnabled(false); enhancer.release(); } catch (Exception ignored) {}
        }
        super.onDestroy();
    }
}
