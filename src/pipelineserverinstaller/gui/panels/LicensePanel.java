package pipelineserverinstaller.gui.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import pipelineserverinstaller.gui.ComponentFactory;

/**
 *
 * @author Zhizhong Liu
 */
public class LicensePanel extends AbstractStepPanel {

    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;

    private String north = SpringLayout.NORTH;
    private String south = SpringLayout.SOUTH;
    private String west = SpringLayout.WEST;
    private String east = SpringLayout.EAST;

    private final int panelMargin = 20;
    private final int dist = 5;
    private final int vDist = 8;

    private JLabel licenseTitleLabel;
    private JTextArea licenseTextArea;
    private JScrollPane licenseScrollPane;
    private JRadioButton agreeButton;
    private JRadioButton disagreeButton;
    private ButtonGroup licenseButtonGroup;

    /** Creates a new instance of LicensePanel */
    public LicensePanel() {
        initComponents();
        initLayout();
        initListeners();
    }

    private void initComponents() {
        licenseTitleLabel = ComponentFactory.label("<html><font size=\"4\"><b>License Agreement</b></font></html>");
        licenseTextArea = ComponentFactory.textarea(2,2);
        licenseTextArea.setText("By registering for downloads from the UCLA Laboratory of Neuro Imaging, you are agreeing to the following terms and conditions as well as to the Terms of Use of the LONI website. To view the LONI website's Terms of Use go to, http://www.loni.ucla.edu/software/termsofuse.php.\n"
                + "\n- Permission is granted to use this software without charge for non-commercial research purposes only.\n"
                + "\n- Other than the rights granted herein, LONI retains all rights, title, and interest in LONI software and Technology, and You retain all rights, title, and interest in Your Modifications and associated specifications, subject to the terms of this License.\n"
                + "\n- You may make verbatim copies of this software for personal use, or for use within your organization, provided that you duplicate all of the original copyright notices and associated disclaimers. If you provide the use of the software to other users within your organization, they also must comply with all the terms of this Software Distribution Agreement.\n"
                + "\n- YOU MAY NOT DISTRIBUTE COPIES of this software, or copies of software derived from this software, to others outside your organization without specific prior written permission from the UCLA Laboratory of Neuro Imaging, except where noted for specific software products.\n"
                + "\n- You must not remove or alter any copyright or other proprietary notices in the software.\n"
                + "\n- Software has not been reviewed or approved by the Food and Drug Administration, and is for non-clinical, IRB-approved Research Use Only. In no event shall data or images generated through the use of the Software be used in the provision of patient care.\n"
                + "\n- THE SOFTWARE IS PROVIDED \"AS IS,\" AND THE UNIVERSITY OF CALIFORNIA AND ITS COLLABORATORS DO NOT MAKE ANY WARRANTY, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, NOR DO THEY ASSUME ANY LIABILITY OR RESPONSIBILITY FOR THE USE OF THIS SOFTWARE.\n"
                + "\n- This software is for research purposes only and has not been approved for clinical use.\n"
                + "\n- You may publish papers and books using results produced using software provided by this site provided that you reference the appropriate citations. A list of citations is available at ( http://www.loni.ucla.edu/Software/fundingcitations.php ).\n"
                + "\n- You agree to comply with LONI's Trademark Usage Requirements, as modified from time to time, described in the \"Use of Materials Limitations\" section of the LONI Terms of Use Agreement. Except as expressly provided in this License, you are granted no rights in or to any LONI trademarks now or hereafter used or licensed by LONI.\n"
                + "\n- All Technology and technical data delivered under this Agreement are subject to US export control laws and may be subject to export or import regulations in other countries. You agree to comply strictly with all such laws and regulations and acknowledge that you have the responsibility to obtain such licenses to export, re-export, or import as may be required after delivery to you.\n");
        licenseTextArea.setLineWrap(true);
        licenseTextArea.setWrapStyleWord(true);
        licenseTextArea.setEditable(false);
        licenseScrollPane = new JScrollPane(licenseTextArea);
        licenseScrollPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        licenseScrollPane.setPreferredSize(new Dimension(100, 150));

        agreeButton = new JRadioButton("I Agree");
        disagreeButton = new JRadioButton("I Disagree");
        licenseButtonGroup = new ButtonGroup();
        licenseButtonGroup.add(agreeButton);
        licenseButtonGroup.add(disagreeButton);

        this.setPreferredSize(new Dimension(400, 400));
    }


    private void initLayout() {
        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        add(licenseTitleLabel);
        layout.putConstraint(north, licenseTitleLabel, panelMargin, north, this);
        layout.putConstraint(west, licenseTitleLabel, panelMargin, west, this);

        add(licenseScrollPane);
        layout.getConstraints(licenseScrollPane).setConstraint(east, Spring.sum(Spring.constant(-panelMargin), layout.getConstraint(east, this)));
        layout.getConstraints(licenseScrollPane).setConstraint(south, Spring.sum(Spring.constant(-vDist*2), layout.getConstraint(north, agreeButton)));
        layout.putConstraint(north, licenseScrollPane, vDist, south, licenseTitleLabel);
        layout.putConstraint(west, licenseScrollPane, 0, west, licenseTitleLabel);

        add(agreeButton);
        layout.putConstraint(south, agreeButton, -panelMargin, south, this);
        layout.putConstraint(east, agreeButton, -panelMargin*6, east, this);
        add(disagreeButton);
        layout.putConstraint(south, disagreeButton, 0, south, agreeButton);
        layout.putConstraint(east, disagreeButton, -panelMargin*2, west, agreeButton);

    }


    private void initListeners() {
        agreeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                agreeDisagree(true);
            }
        });
        disagreeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                agreeDisagree(false);
            }
        });

    }

    private void agreeDisagree(boolean agree) {
        sif.setNextEnabled(agree);
    }

    public void saveUserInput() {

    }
    
    public boolean checkUserInput() {
        return true;
    }

    public void panelActivated() {
        if ( agreeButton.isSelected() )
            sif.setNextEnabled(true);
    }
}

