package com.weather;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class SearchCityFrame extends JPanel {

    private CardLayout cardLayout;
    private JPanel cards;
    private ShowCityFrame showCityFrame;
    private JTextField cityInputField = new JTextField();
    private JScrollPane scrollPane = new JScrollPane();
    private JPanel weatherDisplayArea = new JPanel();

    public SearchCityFrame(CardLayout cardLayout, JPanel cards, ShowCityFrame showCityFrame) {
        this.cardLayout = cardLayout;
        this.cards = cards;
        this.showCityFrame = showCityFrame;

        setSize(400, 300);
        setLayout(new BorderLayout());

        add(cityInputField, BorderLayout.NORTH);

        weatherDisplayArea.setLayout(new BoxLayout(weatherDisplayArea, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(weatherDisplayArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateWeather();
            }
        });
        timer.setRepeats(false);

        cityInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timer.stop();
                timer.start();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timer.stop();
                timer.start();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timer.stop();
                timer.start();
            }
        });

    }

    private void updateWeather() {
        String city = cityInputField.getText();
        if (city == "") {
            return;
        }
        try {
            JSONObject cityInfo = WeatherService.getWeather(city, "city");
            weatherDisplayArea.removeAll();
            Iterator<Object> iterator = cityInfo.getJSONArray("location").iterator();
            while (iterator.hasNext()) {
                JSONObject item = (JSONObject) iterator.next();
                String cityID = item.getString("id");
                System.out.print(item.getString("name"));
                JSONObject weatherData = WeatherService.getWeather(cityID, "weather");
                JButton button = new JButton(String.format("%s  %s℃  %s",
                        weatherData.getJSONObject("now").getString("text"),
                        weatherData.getJSONObject("now").getString("temp"),
                        item.getString("name")));
                button.setOpaque(false);
                button.setBackground(null);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setMinimumSize(new Dimension(400, 30));
                button.setMaximumSize(new Dimension(400, 30));
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showCityFrame.setCurCity(cityID);
                        showCityFrame.updateCity();
                        cardLayout.show(cards, "ShowCityFrame");
                    }
                });
                weatherDisplayArea.add(button);
            }
            weatherDisplayArea.revalidate();
            weatherDisplayArea.repaint();
        } catch (Exception ex) {
        }
    }

}
