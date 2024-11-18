package com.weather;

import javax.swing.*;

import org.json.JSONObject;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import java.time.LocalDateTime;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ShowCityFrame extends JPanel {

    private String curCity;
    private JSONObject weatherData;
    private JSONObject cityInfo;
    private JSONObject warningData;
    private FlatSVGIcon svgIcon;
    private float hue;
    private JLabel jLabel = new JLabel();
    private JLabel humidityArea = new JLabel();
    private JLabel windScaleArea = new JLabel();
    private JLabel visArea = new JLabel();
    private JLabel weatherDisplayArea1 = new JLabel();
    private JLabel weatherDisplayArea2 = new JLabel();
    private JTextArea warningArea = new JTextArea();
    private JButton futurePredict = new JButton();
    private JButton changeCityButton = new JButton();
    private JButton updateButton = new JButton();
    private JScrollPane scrollPane = new JScrollPane();
    private ClassLoader classLoader = ShowCityFrame.class.getClassLoader();

    public ShowCityFrame(CardLayout cardLayout, JPanel cards, WeatherPredictFrame weatherPredictFrame) {
        setLayout(null);

        jLabel.setBounds(30, -20, 400, 300);
        add(jLabel);

        curCity = "101010100";

        addLabel(new JLabel("相对湿度"), new Font("Serif", Font.BOLD, 20), 0, 0, 100, 30, this);
        addLabel(new JLabel("  风速"), new Font("Serif", Font.BOLD, 20), 100, 0, 100, 30, this);
        addLabel(new JLabel(" 能见度"), new Font("Serif", Font.BOLD, 20), 200, 0, 100, 30, this);
        addLabel(humidityArea, new Font("Serif", Font.BOLD, 20), 0, 30, 100, 30, this);
        addLabel(windScaleArea, new Font("Serif", Font.BOLD, 20), 100, 30, 100, 30, this);
        addLabel(visArea, new Font("Serif", Font.BOLD, 20), 200, 30, 100, 30, this);
        addLabel(weatherDisplayArea1, new Font("Serif", Font.BOLD, 30), 200, 75, 200, 75, this);
        addLabel(weatherDisplayArea2, new Font("Serif", Font.BOLD, 30), 200, 125, 200, 75, this);
        addLabel(new JLabel("      未来预测->"), new Font("Serif", Font.BOLD, 15), 270, 245, 125, 27, this);
        addLabel(new JLabel("      更新天气"), new Font("Serif", Font.BOLD, 15), 145, 245, 125, 27, this);
        addLabel(new JLabel("    更改城市"), new Font("Serif", Font.BOLD, 15), 300, 0, 95, 60, this);

        warningArea.setEditable(false);
        warningArea.setFocusable(false);
        warningArea.setLineWrap(true);
        scrollPane = new JScrollPane(warningArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(0, 200, 145, 72);
        scrollPane.setVisible(false);
        add(scrollPane);

        futurePredict.setBounds(270, 245, 125, 27);
        futurePredict.setOpaque(false);
        futurePredict.setBorderPainted(false);
        futurePredict.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weatherPredictFrame.setCityID(curCity);
                weatherPredictFrame.updataPredict(hue);
                cardLayout.show(cards, "WeatherPredictFrame");
            }
        });
        add(futurePredict);

        changeCityButton.setBounds(300, 0, 95, 60);
        changeCityButton.setBorderPainted(false);
        changeCityButton.setOpaque(false);
        changeCityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "SearchCityFrame");
            }
        });
        add(changeCityButton);

        updateButton.setBounds(145, 245, 125, 27);
        updateButton.setOpaque(false);
        updateButton.setBorderPainted(false);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCity();
            }
        });
        add(updateButton);

        updateCity();
    }

    public static void addLabel(JLabel textLabel, Font font, int x, int y, int width, int height, JPanel panel) {
        textLabel.setAlignmentX(.5f);
        textLabel.setFont(font);
        textLabel.setBounds(x, y, width, height);
        panel.add(textLabel);
    }

    public void updateCity() {
        try {
            weatherData = WeatherService.getWeather(curCity, "weather");
            cityInfo = WeatherService.getWeather(curCity, "city");
            warningData = WeatherService.getWeather(curCity, "warning");
            String warningText;
            try {
                warningText = warningData.getJSONArray("warning").getJSONObject(0).getString("text");
                warningArea.setText(warningText);
                scrollPane.setVisible(true);
            } catch (Exception ex) {
                scrollPane.setVisible(false);
            }
            humidityArea.setText(String.format("   %s%%", weatherData.getJSONObject("now").getString("humidity")));
            windScaleArea
                    .setText(String.format("%skm/s", weatherData.getJSONObject("now").getString("windScale")));
            visArea.setText(String.format("  %skm", weatherData.getJSONObject("now").getString("vis")));
            weatherDisplayArea2.setText(String.format(" %s℃", weatherData.getJSONObject("now").getString("temp")));
            weatherDisplayArea1.setText(
                    String.format(" %s %s", cityInfo.getJSONArray("location").getJSONObject(0).getString("name"),
                            weatherData.getJSONObject("now").getString("text")));

            svgIcon = new FlatSVGIcon(String.format("icons/%s.svg", weatherData.getJSONObject("now").getString("icon")),
                    100, 100, classLoader);
            int hour = LocalDateTime.now().getHour();
            if (hour >= 6 && hour < 12) {
                hue = (float) (0.1 + (hour - 6) * 0.01);
            } else if (hour >= 12 && hour < 18) {
                hue = (float) (0.16 - (hour - 12) * 0.01);
            } else if (hour >= 18 && hour < 21) {
                hue = (float) (0.5 + (hour - 18) * 0.06);
            } else {
                hue = 0.7f;
            }
            svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.getHSBColor(hue, 0.6f, 0.8f)));
            jLabel.setIcon((ImageIcon) svgIcon);
            warningArea.setBackground(Color.getHSBColor(hue, 0.3f, 0.8f));
            futurePredict.setBackground(Color.getHSBColor(hue, 0.3f, 0.8f));
            changeCityButton.setBackground(Color.getHSBColor(hue, 0.3f, 0.8f));
            updateButton.setBackground(Color.getHSBColor(hue, 0.3f, 0.8f));
            setBackground(Color.getHSBColor(hue, 0.3f, 0.8f));
        } catch (Exception ex) {
        }
    }

    public void setCurCity(String curCity) {
        this.curCity = curCity;
    }

}
