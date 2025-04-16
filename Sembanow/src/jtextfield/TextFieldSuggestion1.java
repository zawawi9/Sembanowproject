package jtextfield;

import javax.swing.JTextField;

public class TextFieldSuggestion1 extends JTextField {

    private TextFieldSuggestionUI1 textUI;

    public TextFieldSuggestion1() {
        textUI = new TextFieldSuggestionUI1(this);
        setUI(textUI);
    }

    public void addItemSuggestion(String text) {
        textUI.getItems().add(text);
    }

    public void removeItemSuggestion(String text) {
        textUI.getItems().remove(text);
    }

    public void clearItemSuggestion() {
        textUI.getItems().clear();
    }

    

}
