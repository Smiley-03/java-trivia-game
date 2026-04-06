package com.example.triviagame;

import java.io.Serializable;

// Trivia class to store one trivia question
public class Trivia implements Serializable {

    // instance variables
    private String question;
    private String answer;
    private int points;

    // Constructor to initialize instance variables
    public Trivia() {
        question = "";
        answer = "";
        points = 1;
    }

    // Overloaded constructor
    public Trivia(String q, String a, int p) {
        question = q;
        answer = a;
        setPoints(p); // use setter to keep it 1–3
    }


    public String getQuestion() {
        return question;
    }


    public String getAnswer() {
        return answer;
    }


    public int getPoints() {
        return points;
    }


    public void setQuestion(String q) {
        question = q;
    }


    public void setAnswer(String a) {
        answer = a;
    }


    public void setPoints(int p) {
        if (p < 1) {
            points = 1;
        } else if (p > 3) {
            points = 3;
        } else {
            points = p;
        }
    }


    public String toString() {
        return "Question: " + question +
                " | Answer: " + answer +
                " | Points: " + points;
    }
}