package com.weather;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.awt.event.ActionEvent;

import javax.swing.*;

import org.json.JSONObject;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class WeatherPredictFrame extends JPanel {

    private String cityID;
    private float hue;
    private int[] temps = new int[5];
    private JLabel[] jLabels = new JLabel[20];
    private JButton backButton = new JButton();
    private JPanel displayArea;
    private ClassLoader classLoader = ShowCityFrame.class.getClassLoader();

    public WeatherPredictFrame(CardLayout cardLayout, JPanel cards) {
        setLayout(null);

        displayArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.getHSBColor(hue, 0.8f, 0.8f));
                g2d.setStroke(new BasicStroke(2));
                for (int i = 0; i < 4; i++) {
                    g2d.drawLine(20 + 60 * i, 160 - 2 * temps[i], 80 + 60 * i, 160 - 2 * temps[i + 1]);
                }
            }
        };
        displayArea.setBounds(50, 50, 300, 200);
        displayArea.setBackground(new Color(1, 1, 1, .5f));
        add(displayArea);

        ShowCityFrame.addLabel(new JLabel("      <-返回 "), new Font("Serif", Font.BOLD, 15), 0, 0, 125, 27, this);

        for (int i = 0; i < 5; i++) {
            jLabels[i] = new JLabel();
            jLabels[i + 5] = new JLabel();
            jLabels[i + 10] = new JLabel();
            jLabels[i + 15] = new JLabel();
            ShowCityFrame.addLabel(jLabels[i], new Font("Serif", Font.BOLD, 15), 50 + 60 * i, 90, 60, 30, this);
            ShowCityFrame.addLabel(jLabels[i + 5], new Font("Serif", Font.BOLD, 15), 65 + 60 * i, 60, 30, 30, this);
            ShowCityFrame.addLabel(jLabels[i + 10], new Font("Serif", Font.BOLD, 15), 50 + 60 * i, 120, 60, 30, this);
            ShowCityFrame.addLabel(jLabels[i + 15], new Font("Serif", Font.BOLD, 15), 50 + 60 * i, 210, 60, 30, this);
        }

        backButton.setBounds(0, 0, 125, 27);
        backButton.setOpaque(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "ShowCityFrame");
            }
        });
        add(backButton);
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public void updataPredict(float hue) {
        this.hue = hue;
        setBackground(Color.getHSBColor(hue, 0.3f, 0.8f));
        backButton.setBackground(Color.getHSBColor(hue, 0.3f, 0.8f));
        try {
            JSONObject weatherPredict = WeatherService.getWeather(cityID, "24h");
            FlatSVGIcon svgIcon;
            for (int i = 0; i < 5; i++) {
                svgIcon = new FlatSVGIcon(
                        String.format("icons/%s.svg",
                                weatherPredict.getJSONArray("hourly").getJSONObject(i).getString("icon")),
                        30, 30, classLoader);
                svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.getHSBColor(hue, 0.8f, 0.8f)));
                jLabels[i + 5].setIcon((ImageIcon) svgIcon);
                jLabels[i].setText("    " + weatherPredict.getJSONArray("hourly").getJSONObject(i).getString("text"));
                jLabels[i + 10].setText(
                        "   " + weatherPredict.getJSONArray("hourly").getJSONObject(i).getString("temp") + "℃");
                jLabels[i + 15].setText("  " + (LocalDateTime.now().getHour() + i) + ":00");
                temps[i] = Integer.parseInt(weatherPredict.getJSONArray("hourly").getJSONObject(i).getString("temp"));
            }
            displayArea.repaint();
        } catch (Exception ex) {
        }
    }
}
