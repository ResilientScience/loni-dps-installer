package pipelineserverinstaller.gui;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import pipelineserverinstaller.Constants;

/**
 *
 * @author Zhizhong Liu
 */
public class ComponentFactory {
    public static JButton button(String text) {
        if (Constants.isLeopard)
        {
            JButton button = new JButton(text);
            button.putClientProperty("JComponent.sizeVariant", "small");
            button.setFont(leopardFont);
            return button;
        }
        else if (Constants.isWindows)
        {
            JButton button = new JButton(text);
            Dimension prefSize = button.getPreferredSize();
            if (prefSize.width < 75)
                button.setPreferredSize(new Dimension(75, prefSize.height));
            return button;
        }
            return new JButton(text);
    }

    public static JCheckBox checkbox(String label) {
        if (Constants.isLeopard)
        {
            JCheckBox checkbox = new JCheckBox(label);
            checkbox.putClientProperty("JComponent.sizeVariant", "small");
            checkbox.setFont(leopardFont);
            return checkbox;
        }
        else
            return new JCheckBox(label);
    }

    public static JComboBox combobox() {
        if (Constants.isLeopard)
        {
            JComboBox combobox = new JComboBox();
            combobox.putClientProperty("JComponent.sizeVariant", "small");
            combobox.setFont(leopardFont);
            return combobox;
        }
        else
            return new JComboBox();
    }

    public static JComboBox combobox(ComboBoxModel model) {
        if (Constants.isLeopard)
        {
            JComboBox combobox = new JComboBox(model);
            combobox.putClientProperty("JComponent.sizeVariant", "small");
            combobox.setFont(leopardFont);
            return combobox;
        }
        else
            return new JComboBox(model);
    }

    public static JLabel label() {
        if (Constants.isLeopard)
        {
            JLabel label = new JLabel();
            label.setFont(leopardFont);
            return label;
        }
        else
            return new JLabel();
    }

    public static JLabel label(String text) {
        if (Constants.isLeopard)
        {
            JLabel label = new JLabel(text);
            label.setFont(leopardFont);
            return label;
        }
        else
            return new JLabel(text);
    }

    public static JPasswordField passwordfield(int columns) {
        if (Constants.isLeopard)
        {
            JPasswordField passwordfield = new JPasswordField(columns);
            passwordfield.setFont(leopardFont);
            return passwordfield;
        }
        else
            return new JPasswordField(columns);
    }

    public static JRadioButton radiobutton(String label) {
        if (Constants.isLeopard)
        {
            JRadioButton radiobutton = new JRadioButton(label);
            radiobutton.putClientProperty("JComponent.sizeVariant", "small");
            radiobutton.setFont(leopardFont);
            return radiobutton;
        }
        else
            return new JRadioButton(label);
    }

    public static JSpinner spinner(SpinnerModel model) {
        if (Constants.isLeopard)
        {
            JSpinner spinner = new JSpinner(model);
            spinner.putClientProperty("JComponent.sizeVariant", "small");
            spinner.setFont(leopardFont);
            return spinner;
        }
        else
            return new JSpinner(model);
    }

    public static JTextArea textarea() {
        if (Constants.isLeopard)
        {
            JTextArea textarea = new JTextArea();
            textarea.setFont(leopardFont);
            return textarea;
        }
        else
            return new JTextArea();
    }

    public static JTextArea textarea(String text) {
        if (Constants.isLeopard)
        {
            JTextArea textarea = new JTextArea(text);
            textarea.setFont(leopardFont);
            return textarea;
        }
        else
            return new JTextArea(text);
    }

    public static JTextArea textarea(int rows, int columns) {
        if (Constants.isLeopard)
        {
            JTextArea textarea = new JTextArea(rows, columns);
            textarea.setFont(leopardFont);
            return textarea;
        }
        else
            return new JTextArea(rows, columns);
    }

    public static JTextField textfield() {
        if (Constants.isLeopard)
        {
            JTextField textfield = new JTextField();
            textfield.setFont(leopardFont);
            return textfield;
        }
        else
            return new JTextField();
    }

    public static JTextField textfield(int columns) {
        if (Constants.isLeopard)
        {
            JTextField textfield = new JTextField(columns);
            textfield.setFont(leopardFont);
            return textfield;
        }
        else
            return new JTextField(columns);
    }

    public static JTextField textfield(String text) {
        if (Constants.isLeopard)
        {
            JTextField textfield = new JTextField(text);
            textfield.setFont(leopardFont);
            return textfield;
        }
        else
            return new JTextField(text);
    }

    private static Font leopardFont = new Font("Lucida Grande", Font.PLAIN, 11);
}
