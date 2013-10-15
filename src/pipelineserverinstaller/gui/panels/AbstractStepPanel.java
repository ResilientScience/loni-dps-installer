
package pipelineserverinstaller.gui.panels;

import javax.swing.JPanel;
import pipelineserverinstaller.gui.ServerInstallerFrame;

/**
 *
 * @author Zhizhong Liu
 */
public abstract class AbstractStepPanel extends JPanel {
    protected ServerInstallerFrame sif;

    public AbstractStepPanel() {
        sif = ServerInstallerFrame.getServerInstallerFrame();
    }

    public abstract boolean checkUserInput();
    public abstract void saveUserInput();

    public abstract void panelActivated();
}
