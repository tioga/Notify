package org.lqnotify.processor.swing;

import org.lqnotify.kernel.processor.ProcessorType;
import org.lqnotify.kernel.processor.TaskProcessor;
import org.lqnotify.pub.DomainProfile;
import org.lqnotify.pub.Notification;
import org.lqnotify.pub.Task;
import org.lqnotify.pub.TaskResponse;
import org.springframework.beans.factory.BeanFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class SwingTaskProcessor implements TaskProcessor {

  private static final ProcessorType PROVIDER_TYPE = new ProcessorType("swing");
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-YYYY HH:mm");

  private JFrame frame;
  private JPanel panel;
  private ArrayList<Notification> notifications = new ArrayList<>();

  public SwingTaskProcessor() {
  }

  @Override
  public boolean isReady() {
    return GraphicsEnvironment.isHeadless() == false;
  }

  @Override
  public ProcessorType getType() {
    return PROVIDER_TYPE;
  }

  @Override
  public void init(BeanFactory beanFactory) {
  }

  @Override
  public synchronized TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {
    if (frame == null) {
      createUI();
    }

    try {
      SwingUtilities.invokeAndWait(() -> {

        notifications.add(notification);

        while (notifications.size() > 50) {
          notifications.remove(0);
        }

        List<Notification> copy = new ArrayList<>(notifications);
        Collections.sort(copy, Collections.reverseOrder());

        panel.removeAll();
        for (Notification next : copy) {
          panel.add(new JLabel(
            next.getCreatedAt().format(formatter) + ": " +
            next.getSummary())
          );
        }

        if (frame.isVisible() == false) {
          frame.setVisible(true);
        }
        frame.toFront();
        frame.validate();
      });

    } catch (InterruptedException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return TaskResponse.complete("Ok");
  }

  private void createUI() {
    if (frame != null) return;

    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(panel);

    JFrame newFrame = new JFrame("Notifications");
    newFrame.getContentPane().add(scrollPane);
    newFrame.setSize(new Dimension(350, 600));

    frame = newFrame;
  }
}
