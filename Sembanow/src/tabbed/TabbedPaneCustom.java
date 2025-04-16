package tabbed;

import java.awt.Color;
import javax.swing.JTabbedPane;

public class TabbedPaneCustom extends JTabbedPane {

    public TabbedPaneCustom() {
        setBackground(new Color(250, 250, 250));
        setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        setUI(new TabbedPaneCustomUI(this));
    }
}