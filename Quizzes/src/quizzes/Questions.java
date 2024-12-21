package quizzes;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Questions extends JFrame implements ActionListener {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private List<Question> questionsList;
    private int currentQuestion = 0;
    private int correctAnswersCount = 0;

    private JLabel title;
    private JLabel questionLabel;
    private JRadioButton option1, option2, option3, option4;
    private ButtonGroup options;
    private JButton nextButton;
    private JButton resultButton;

    public Questions() {
        connectToDatabase();  // Connect to the database
        loadQuestionsFromDatabase(); // Load questions from the database
        openExamFrame();      // Open the exam frame
    }

    // Database connection method
    public void connectToDatabase() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/last?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC", "root", ""); // Update with your database credentials
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    // Load questions from the database
    public void loadQuestionsFromDatabase() {
        questionsList = new ArrayList<>();
        try {
            String query = "SELECT * FROM question";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String questionText = rs.getString("question");
                String option1Text = rs.getString("option_1");
                String option2Text = rs.getString("option_2");
                String option3Text = rs.getString("option_3");
                String option4Text = rs.getString("option_4");
                int correctAnswer = rs.getInt("correct_answer");

                questionsList.add(new Question(questionText, option1Text, option2Text, option3Text, option4Text, correctAnswer));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading questions from database: " + e.getMessage());
        }
    }

    // Open the exam frame
    public void openExamFrame() {
        setTitle("Online Examination System");
        setBounds(300, 90, 600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        Container container = getContentPane();
        container.setLayout(null);

        title = new JLabel("Online Examination");
        title.setFont(new Font("Arial", Font.PLAIN, 24));
        title.setSize(300, 30);
        title.setLocation(200, 30);
        container.add(title);

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        questionLabel.setSize(550, 30);
        questionLabel.setLocation(50, 80);
        container.add(questionLabel);

        option1 = new JRadioButton();
        option1.setFont(new Font("Arial", Font.PLAIN, 15));
        option1.setSize(500, 20);
        option1.setLocation(50, 120);
        container.add(option1);

        option2 = new JRadioButton();
        option2.setFont(new Font("Arial", Font.PLAIN, 15));
        option2.setSize(500, 20);
        option2.setLocation(50, 150);
        container.add(option2);

        option3 = new JRadioButton();
        option3.setFont(new Font("Arial", Font.PLAIN, 15));
        option3.setSize(500, 20);
        option3.setLocation(50, 180);
        container.add(option3);

        option4 = new JRadioButton();
        option4.setFont(new Font("Arial", Font.PLAIN, 15));
        option4.setSize(500, 20);
        option4.setLocation(50, 210);
        container.add(option4);

        options = new ButtonGroup();
        options.add(option1);
        options.add(option2);
        options.add(option3);
        options.add(option4);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 15));
        nextButton.setSize(100, 30);
        nextButton.setLocation(100, 300);
        nextButton.addActionListener(this);
        container.add(nextButton);

        resultButton = new JButton("Show Result");
        resultButton.setFont(new Font("Arial", Font.PLAIN, 15));
        resultButton.setSize(150, 30);
        resultButton.setLocation(300, 300);
        resultButton.addActionListener(this);
        resultButton.setVisible(false);
        container.add(resultButton);

        loadQuestion(currentQuestion); // Load the first question
        setVisible(true);
    }

    // Load a specific question based on the index
    public void loadQuestion(int questionIndex) {
        if (questionIndex >= 0 && questionIndex < questionsList.size()) {
            Question question = questionsList.get(questionIndex);
            questionLabel.setText(question.getQuestionText());
            option1.setText(question.getOption1());
            option2.setText(question.getOption2());
            option3.setText(question.getOption3());
            option4.setText(question.getOption4());
            options.clearSelection();
        } else {
            JOptionPane.showMessageDialog(this, "End of questions reached!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            // Check the current answer before moving to the next question
            if (checkAnswer()) {
                correctAnswersCount++; // Increment if the answer is correct
            }

            // Proceed to the next question
            currentQuestion++;
            if (currentQuestion < questionsList.size()) {
                loadQuestion(currentQuestion);
            } else {
                // Hide next button and show result button
                nextButton.setVisible(false);
                resultButton.setVisible(true);
            }
        } else if (e.getSource() == resultButton) {
            // Show the result with correct answers
            StringBuilder resultMessage = new StringBuilder();
            resultMessage.append("You got ").append(correctAnswersCount).append(" out of ").append(questionsList.size()).append(" correct answers.\n\n");

            // Loop through all questions to show correct answers
            for (int i = 0; i < questionsList.size(); i++) {
                Question question = questionsList.get(i);
                String userAnswer = getSelectedAnswer(i);
                String correctAnswer = getCorrectAnswerText(question.getCorrectAnswer(), question);

                resultMessage.append("Question ").append(i + 1).append(": ").append(question.getQuestionText()).append("\n");
                resultMessage.append("Your Answer: ").append(userAnswer).append("\n");
                resultMessage.append("Correct Answer: ").append(correctAnswer).append("\n\n");
            }

            // Show the result message in a message dialog
            JOptionPane.showMessageDialog(this, resultMessage.toString());
        }
    }

    // Helper method to check if the selected answer is correct
    private boolean checkAnswer() {
        Question question = questionsList.get(currentQuestion);
        int correctAnswerIndex = question.getCorrectAnswer();
        if (correctAnswerIndex == 1 && option1.isSelected()) return true;
        if (correctAnswerIndex == 2 && option2.isSelected()) return true;
        if (correctAnswerIndex == 3 && option3.isSelected()) return true;
        if (correctAnswerIndex == 4 && option4.isSelected()) return true;
        return false;
    }

    // Helper method to get the selected answer for a question
    private String getSelectedAnswer(int questionIndex) {
        Question question = questionsList.get(questionIndex);
        if (option1.isSelected()) return question.getOption1();
        else if (option2.isSelected()) return question.getOption2();
        else if (option3.isSelected()) return question.getOption3();
        else if (option4.isSelected()) return question.getOption4();
        return "No answer"; // In case no option is selected
    }

    // Helper method to get the correct answer text by index
    private String getCorrectAnswerText(int correctAnswerIndex, Question question) {
        switch (correctAnswerIndex) {
            case 1: return question.getOption1();
            case 2: return question.getOption2();
            case 3: return question.getOption3();
            case 4: return question.getOption4();
            default: return "No answer";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Questions();
            }
        });
    }
}

// Question class to hold the question data
class Question {
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int correctAnswer;

    public Question(String questionText, String option1, String option2, String option3, String option4, int correctAnswer) {
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
