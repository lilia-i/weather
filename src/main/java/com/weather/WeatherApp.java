package com.weather;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class WeatherApp extends JFrame {
    WeatherApp() {
        setTitle("Weather App");
        setSize(400, 310);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CardLayout cardLayout = new CardLayout();
        JPanel cards = new JPanel(cardLayout);
        WeatherPredictFrame weatherPredictFrame = new WeatherPredictFrame(cardLayout, cards);
        ShowCityFrame showCityFrame = new ShowCityFrame(cardLayout, cards, weatherPredictFrame);
        SearchCityFrame searchCityFrame = new SearchCityFrame(cardLayout, cards, showCityFrame);
        cards.add(weatherPredictFrame, "WeatherPredictFrame");
        cards.add(searchCityFrame, "SearchCityFrame");
        cards.add(showCityFrame, "ShowCityFrame");
        cardLayout.show(cards, "ShowCityFrame");
        add(cards);
        setVisible(true);
    }

    public static void main(String[] args) {
        new WeatherApp();
        while (true) {

        }
    }
}
