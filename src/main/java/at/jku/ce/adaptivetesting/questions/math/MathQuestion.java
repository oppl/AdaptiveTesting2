package at.jku.ce.adaptivetesting.questions.math;

import at.jku.ce.adaptivetesting.core.IQuestion;
import at.jku.ce.adaptivetesting.core.LogHelper;
import at.jku.ce.adaptivetesting.questions.XmlQuestionData;
import at.jku.ce.adaptivetesting.questions.math.js.GeoGebraComponent;
import at.jku.ce.adaptivetesting.views.html.HtmlLabel;
import com.vaadin.server.Page;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;


import java.io.*;

public class MathQuestion extends VerticalLayout implements IQuestion<MathDataStorage>, Cloneable {
    private static final long serialVersionUID = 6373936654529246422L;
    private float difficulty = 0;
    private Label question;
    private Image questionImage = null;
    private String id;
    private String materialNr;
    private final GeoGebraComponent geogebraComponent = new GeoGebraComponent();
    private double resultEvaluation = 0.0d;

    public MathQuestion (Float difficulty, String questionText,  String materialNr, Image questionImage, String id) {
        this(0f, "", "", null, "");
    }

    public MathQuestion(float difficulty, String questionText, String materialNr, Image questionImage, String id) {
        this.difficulty = difficulty;
        this.id = id;
        this.materialNr = materialNr;
        this.questionImage = questionImage;

        question = new HtmlLabel();
        setQuestionText(question, questionText);
        addComponent(question);


        //geogebraComponent.setHeight("40%");
        //geogebraComponent.setWidth("40%");
        geogebraComponent.setSizeFull();

        // Set material ID
        geogebraComponent.setValue(materialNr);

        // Add value change listener, on value change the result of the exercise evaluation
        // is set to the value in the shared state of the GeoGebra component.
        geogebraComponent.addValueChangeListener(
                new GeoGebraComponent.ValueChangeListener() {
                    @Override
                    public void valueChange() {
                        resultEvaluation = Double.parseDouble(geogebraComponent.getValue());
                        LogHelper.logInfo("Loaded value from GeoGebra!: " + geogebraComponent.getValue());
                        LogHelper.logInfo("Value changed to!: " + resultEvaluation);
                    }
                });

        addComponent(geogebraComponent);


        if (questionImage != null) addComponent(this.questionImage);

        setSpacing(true);


    }

    @Override
    public String getQuestionID() {
        return id;
    }

    public MathQuestion clone() throws CloneNotSupportedException {
        MathQuestion objClone = (MathQuestion) super.clone();
        return objClone;
    }

    @SuppressWarnings("unchecked")
    public MathQuestion cloneThroughSerialize(MathQuestion t) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializeToOutputStream(t, bos);
        byte[] bytes = bos.toByteArray();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        return (MathQuestion) ois.readObject();
    }

    private static void serializeToOutputStream(Serializable ser, OutputStream os) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject(ser);
            oos.flush();
        } finally {
            oos.close();
        }
    }

    @Override
    public String getQuestionText() {
        return question.getValue();
    }

    public void setText(Label label, String text) {
        label.setValue(text);
    }

    public void setQuestionText(Label label, String text) {
        label.setValue("<p style=\"color:#0099ff\">" + text + "</p>");
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public MathDataStorage getUserAnswer() {
        MathDataStorage dataStorage = new MathDataStorage();
        return dataStorage;
    }

    @Override
    public MathDataStorage getSolution() {
        MathDataStorage dataStorage = new MathDataStorage();
        return dataStorage;
    }

    @Override
    public double performQueryDiagnosis() {
        return 0;
    }

    @Override
    public double checkUserAnswer() {
        return resultEvaluation;
    }

    @Override
    public float getDifficulty() {
        return difficulty;
    }

    @Override
    public XmlQuestionData<MathDataStorage> toXMLRepresentation() {
        return new MathQuestionXml(getSolution(), getQuestionText(),
                getDifficulty(), materialNr);
    }

    @Override
    public double getMaxPoints() {
        return 1d;
    }

    public Image getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(Image questionImage) {
        if (questionImage == null) return;
        this.questionImage = questionImage;
        removeAllComponents();
        addComponent(question);
        addComponent(this.questionImage);
    }

    // Custom Notification that stays on-screen until user presses it
    private void createAndShowNotification(String caption, String description, Notification.Type type) {
        description += "<span style=\"position:fixed;top:0;left:0;width:100%;height:100%\"></span>";
        Notification notif = new Notification(caption, description, type, true);
        notif.setDelayMsec(-1);
        notif.show(Page.getCurrent());
    }
}