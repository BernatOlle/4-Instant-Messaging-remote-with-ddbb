package main;

import entity.Message;
import util.Subscription_check;
import entity.Topic;
import subscriber.SubscriberImpl;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import publisher.Publisher;
import subscriber.Subscriber;
import topicmanager.TopicManager;
import topicmanager.TopicManagerStub;
import util.Subscription_close;
import util.Topic_check;
import webSocketService.WebSocketClient;

public class SwingClient {

    TopicManager topicManager;
    public Map<Topic, Subscriber> my_subscriptions;
    public Map<Topic, Publisher> my_publishers; // Store publishers for multiple topics
    Topic publisherTopic;

    JFrame frame;
    JTextArea topic_list_TextArea;
    public JTextArea messages_TextArea;
    public JTextArea info_TextArea;
    JComboBox<Topic> publisherComboBox;
    public JTextArea my_subscriptions_TextArea;
    JTextField argument_TextField;

    public SwingClient(TopicManager topicManager) {
        this.topicManager = topicManager;
        my_subscriptions = new HashMap<Topic, Subscriber>();
        my_publishers = new HashMap<Topic, Publisher>();
        publisherTopic = null;
    }

    public void createAndShowGUI() {

        String login = ((TopicManagerStub) topicManager).user.getLogin();
        frame = new JFrame("Publisher/Subscriber demo, user : " + login);
        frame.setSize(300, 300);
        frame.addWindowListener(new CloseWindowHandler());

        topic_list_TextArea = new JTextArea(5, 10);
        messages_TextArea = new JTextArea(10, 20);
        my_subscriptions_TextArea = new JTextArea(5, 10);
        publisherComboBox = new JComboBox<Topic>();
        argument_TextField = new JTextField(20);

        messages_TextArea = new JTextArea(10, 20);
        messages_TextArea.setEditable(false);
        messages_TextArea.setLineWrap(true);
        messages_TextArea.setWrapStyleWord(true);

        info_TextArea = new JTextArea(10, 20);
        info_TextArea.setEditable(false);
        info_TextArea.setLineWrap(true);
        info_TextArea.setWrapStyleWord(true);

        JButton show_topics_button = new JButton("show Topics");
        JButton new_publisher_button = new JButton("new Publisher");
        JButton new_subscriber_button = new JButton("new Subscriber");
        JButton to_unsubscribe_button = new JButton("to unsubscribe");
        JButton to_post_an_event_button = new JButton("post an event");
        JButton to_close_the_app = new JButton("close app.");

        show_topics_button.addActionListener(new showTopicsHandler());
        new_publisher_button.addActionListener(new newPublisherHandler());
        new_subscriber_button.addActionListener(new newSubscriberHandler());
        to_unsubscribe_button.addActionListener(new UnsubscribeHandler());
        to_post_an_event_button.addActionListener(new postEventHandler());
        to_close_the_app.addActionListener(new CloseAppHandler());
        
        

        JPanel buttonsPannel = new JPanel(new FlowLayout());
        buttonsPannel.add(show_topics_button);
        buttonsPannel.add(new_publisher_button);
        buttonsPannel.add(new_subscriber_button);
        buttonsPannel.add(to_unsubscribe_button);
        buttonsPannel.add(to_post_an_event_button);
        buttonsPannel.add(to_close_the_app);

        JPanel argumentP = new JPanel(new FlowLayout());
        argumentP.add(new JLabel("Write content to set a new_publisher / new_subscriber / unsubscribe / post_event:"));
        argumentP.add(argument_TextField);

        JPanel topicsP = new JPanel();
        topicsP.setLayout(new BoxLayout(topicsP, BoxLayout.PAGE_AXIS));
        topicsP.add(new JLabel("Topics:"));
        topicsP.add(topic_list_TextArea);
        topicsP.add(new JScrollPane(topic_list_TextArea));
        topicsP.add(new JLabel("My Subscriptions:"));
        topicsP.add(my_subscriptions_TextArea);
        topicsP.add(new JScrollPane(my_subscriptions_TextArea));
        topicsP.add(new JLabel("I'm Publisher of topic:"));
        topicsP.add(publisherComboBox);
        
        // Information Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.add(new JLabel("Information:"));
        infoPanel.add(new JScrollPane(info_TextArea));

        JPanel messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.PAGE_AXIS));
        messagesPanel.add(new JLabel("Messages:"));
        messagesPanel.add(messages_TextArea);
        messagesPanel.add(new JScrollPane(messages_TextArea));

       // SplitPane for Columns
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoPanel, messagesPanel);
        splitPane.setDividerLocation(150); // Initial division point
        splitPane.setResizeWeight(0.3); // Allocate 30% of space to the info panel

        Container mainPanel = frame.getContentPane();
        mainPanel.add(buttonsPannel, BorderLayout.PAGE_START);
        mainPanel.add(splitPane, BorderLayout.CENTER); // Use SplitPane for the center
        mainPanel.add(argumentP, BorderLayout.PAGE_END);
        mainPanel.add(topicsP, BorderLayout.LINE_START);

        //this is where you restore the user profile:
        clientSetup();

        frame.pack();
        frame.setVisible(true);
    }

    private void clientSetup() {
        //this is where you restore the user profile:

        //...
    }

    class showTopicsHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            // Obtén la lista de tópicos (sin el cast)
            List<Topic> topicsList = topicManager.topics();

            if (topicsList == null) {
                topic_list_TextArea.append("No topics available.\n");
            }

            // Ahora puedes trabajar con 'topicsList' como una lista
            for (Topic topic : topicsList) {
                System.out.println(topic);
            }

            // Limpiar el área de texto antes de agregar los nuevos datos
            topic_list_TextArea.setText("");

            // Añadir los nombres de los tópicos al área de texto
            for (Topic topic : topicsList) {
                topic_list_TextArea.append(topic.name + "\n");
            }

        }
    }

    class newPublisherHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String topicName = argument_TextField.getText().trim();

            // Check for empty topic name
            if (topicName.isEmpty()) {
                info_TextArea.append("Error: Topic name cannot be empty.\n");
                return;
            }

            Topic selectedTopic = new Topic(topicName);

            // Check if the topic exists using the API
            Topic_check topicCheck = topicManager.isTopic(selectedTopic);

            if (topicCheck.isOpen) {
                // If the topic exists, check if the user is already a publisher
                if (my_publishers.containsKey(selectedTopic)) {
                    info_TextArea.append("You are already a publisher for topic: " + topicName + "\n");
                    publisherComboBox.setSelectedItem(selectedTopic);
                    return;
                }

                // Add the user as a publisher to the existing topic
                Publisher newPublisher = topicManager.addPublisherToTopic(selectedTopic);
                if (newPublisher != null) {
                    publisherComboBox.addItem(selectedTopic);
                    my_publishers.put(selectedTopic, newPublisher);
                    publisherTopic = selectedTopic;
                    info_TextArea.append("You are now a publisher for the existing topic: " + topicName + "\n");
                    publisherComboBox.setSelectedItem(selectedTopic);
                } else {
                    info_TextArea.append("Error: Failed to assign you as a publisher for the topic: " + topicName + "\n");
                }
            } else {
                // If the topic does not exist, create it and assign the user as its publisher
                Publisher newPublisher = topicManager.addPublisherToTopic(selectedTopic);
                if (newPublisher != null) {
                    publisherComboBox.addItem(selectedTopic);
                    my_publishers.put(selectedTopic, newPublisher);
                    publisherTopic = selectedTopic;
                    info_TextArea.append("Topic '" + topicName + "' was created, and you are now its publisher.\n");
                    publisherComboBox.setSelectedItem(selectedTopic);
                } else {
                    info_TextArea.append("Error: Failed to create and assign you as a publisher for the topic: " + topicName + "\n");
                }
            }

        }
    }

    class newSubscriberHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String topicName = argument_TextField.getText().trim();
            Topic topic = new Topic(topicName);
            Subscriber subscriber = new SubscriberImpl(SwingClient.this);
            Subscription_check result = topicManager.subscribe(topic, subscriber);
            System.out.print("My subscriptions "+my_subscriptions+" "+result.result);
            if (result.result == Subscription_check.Result.OKAY) {
                if (my_subscriptions.containsKey(topic)) {
                    info_TextArea.append("Already subcribed to topic: " + topicName + "\n");

                } else {

                    my_subscriptions.put(topic, subscriber);
                    my_subscriptions_TextArea.setText("");
                    for (Topic t : my_subscriptions.keySet()) {
                        my_subscriptions_TextArea.append(t.name + "\n");
                    }
                    info_TextArea.append("Successfully subscribed to topic: " + topicName + "\n");
                }
            } else if (result.result == Subscription_check.Result.NO_TOPIC) {
                info_TextArea.append("Error: Topic '" + topicName + "' does not exist.\n");
            } else if (result.result == Subscription_check.Result.NO_SUBSCRIPTION) {
                info_TextArea.append("Server Error: Not able to complete Subscription.\n");
            }

        }
    }

    class UnsubscribeHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String topicName = argument_TextField.getText().trim();
            Topic topic = new Topic(topicName);
            Subscriber subscriber = my_subscriptions.get(topic);
            if (subscriber == null) {
                info_TextArea.append("Error: You are not subscribed to topic '" + topicName + "'.\n");
                return;
            }

            Subscription_check result = topicManager.unsubscribe(topic, subscriber);
            
            if (result.result == Subscription_check.Result.OKAY) {
                System.out.print("unsubscribe");
                my_subscriptions.remove(topic);
                Subscription_close subs_close = new Subscription_close(topic, Subscription_close.Cause.SUBSCRIBER);
                subscriber.onClose(subs_close);
                my_subscriptions_TextArea.setText("");
                for (Topic t : my_subscriptions.keySet()) {
                    my_subscriptions_TextArea.append(t.name + "\n");
                }

                info_TextArea.append("Successfully unsubscribed from topic: " + topicName + "\n");
            } else if (result.result == Subscription_check.Result.NO_TOPIC) {
                info_TextArea.append("Error: Topic '" + topicName + "' does not exist.\n");
            }

        }
    }

    class postEventHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if (publisherTopic == null) {
                info_TextArea.append("Error: No topic selected for publishing.\n");
                return;
            }

            Publisher publisher = my_publishers.get(publisherTopic);
            if (publisher == null) {
                info_TextArea.append("Error: No publisher found for the selected topic.\n");
                return;
            }

            String content = argument_TextField.getText();
            Message message = new Message(publisherTopic, content);

            publisher.publish(message);
            messages_TextArea.append("Message posted to topic '" + publisherTopic.name + "': " + content + "\n");
           

        }
    }

    class CloseAppHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            for (Topic t : my_subscriptions.keySet()) {
                Subscriber subscriber = my_subscriptions.get(t);
                topicManager.unsubscribe(t, subscriber);

            }
            topicManager.removePublisherFromTopic(publisherTopic);
            System.out.println("all users closed");
            System.exit(0);
        }
    }

    class CloseWindowHandler implements WindowListener {

        public void windowDeactivated(WindowEvent e) {
        }

        public void windowActivated(WindowEvent e) {
        }

        public void windowIconified(WindowEvent e) {
        }

        public void windowDeiconified(WindowEvent e) {
        }

        public void windowClosed(WindowEvent e) {
        }

        public void windowOpened(WindowEvent e) {
        }

        public void windowClosing(WindowEvent e) {

            //...
            System.out.println("one user closed");
            System.exit(0);
        }
    }
}
