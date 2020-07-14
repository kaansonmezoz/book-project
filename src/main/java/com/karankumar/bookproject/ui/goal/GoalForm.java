package com.karankumar.bookproject.ui.goal;

import com.karankumar.bookproject.backend.entity.ReadingGoal;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.shared.Registration;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Vaadin view that contains a form for setting a new reading goal
 */
public class GoalForm extends VerticalLayout {

    private static final Logger LOGGER = Logger.getLogger(GoalForm.class.getName());
    private static final String BOOKS_TO_READ = "Books to read";
    private static final String PAGES_TO_READ = "Pages to read";

    private final Dialog newGoalDialog;
    private final Binder<ReadingGoal> binder = new BeanValidationBinder<>(ReadingGoal.class);
    private final RadioButtonGroup<ReadingGoal.GoalType> chooseGoalType;
    private final IntegerField targetToRead;

    public GoalForm() {
        targetToRead = createBooksGoalField();
        Button saveButton = createSaveButton();

        FormLayout formLayout = new FormLayout();
        newGoalDialog = new Dialog();
        newGoalDialog.add(formLayout);
        newGoalDialog.setCloseOnOutsideClick(true);
        add(newGoalDialog);

        chooseGoalType = createGoalTypeRadioGroup();

        formLayout.addFormItem(chooseGoalType, "Goal type");
        formLayout.addFormItem(targetToRead, "To read");
        formLayout.add(new HorizontalLayout(saveButton));
        chooseGoalType.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                return;
            } else if (event.getValue().equals(ReadingGoal.GoalType.BOOKS)) {
                targetToRead.setPlaceholder(BOOKS_TO_READ);
            } else {
                targetToRead.setPlaceholder(PAGES_TO_READ);
            }
        });

        configureBinder();
    }

    /**
     * @return an IntegerField representing the number of books to read
     */
    private IntegerField createBooksGoalField() {
        IntegerField field = new IntegerField();
        field.setPlaceholder(BOOKS_TO_READ);
        field.setClearButtonVisible(true);
        field.setMin(1);
        field.setHasControls(true);
        field.setMinWidth("13em");
        return field;
    }

    /**
     * @return a save button
     */
    private Button createSaveButton() {
        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setMinWidth("13em");
        save.addClickListener(click -> {
            bindIntegerFields();
            validateOnSave();
        });
        return save;
    }

    /**
     * @return a radio button group for the goal type
     */
    private RadioButtonGroup<ReadingGoal.GoalType> createGoalTypeRadioGroup() {
        RadioButtonGroup<ReadingGoal.GoalType> goalTypeRadioButtonGroup = new RadioButtonGroup<>();
        goalTypeRadioButtonGroup.setItems(ReadingGoal.GoalType.values());
        return goalTypeRadioButtonGroup;
    }

    private void bindIntegerFields() {
        if (chooseGoalType.getValue() != null) {
            ReadingGoal.GoalType goalType = this.chooseGoalType.getValue();
            if (goalType.equals(ReadingGoal.GoalType.BOOKS)) {
                LOGGER.log(Level.INFO, "Radio group option chosen: books");
                binder.forField(targetToRead)
                      .asRequired("Please enter in a books goal")
                      .bind(ReadingGoal::getTarget, ReadingGoal::setTarget);
            } else {
                LOGGER.log(Level.INFO, "Radio group option chosen: pages");
                binder.forField(targetToRead)
                      .asRequired("Please enter in a pages goal")
                      .bind(ReadingGoal::getTarget, ReadingGoal::setTarget);
            }
        }
    }

    /**
     * Fires a save event if the binder is valid and the bean from the binder is not null
     */
    private void validateOnSave() {
        if (binder.isValid()) {

            if (binder.getBean() == null) {
                LOGGER.log(Level.SEVERE, "Binder reading goal bean is null");
                ReadingGoal.GoalType goalType = this.chooseGoalType.getValue();
                if (goalType == null) {
                    LOGGER.log(Level.SEVERE, "Goal type not set");
                    return;
                } else if (targetToRead.getValue() != null) {
                    binder.setBean(new ReadingGoal(targetToRead.getValue(), goalType));
                }

                LOGGER.log(Level.INFO, "Setting the bean");
            } else {
                LOGGER.log(Level.INFO, "Binder reading goal bean is not null");
            }

            fireEvent(new SaveEvent(this, binder.getBean()));
            confirmSavedGoal(binder.getBean().getTarget(), binder.getBean().getGoalType());
        } else {
            BinderValidationStatus<ReadingGoal> status = binder.validate();
            if (status.hasErrors()) {
                LOGGER.log(Level.SEVERE, "Invalid binder: " + status.getValidationErrors());
            } else {
                LOGGER.log(Level.SEVERE, "Invalid binder. No status errors");
            }
        }
    }

    /**
     * Displays a confirmation message when the reading goal has been successfully set
     */
    private void confirmSavedGoal(int target, ReadingGoal.GoalType goalType) {
        newGoalDialog.close();
        String notificationMessage =
                String.format("Set your reading goal of %d %s", target, goalType.toString().toLowerCase());
        new Notification(notificationMessage, 3000).open();
    }

    public void openForm() {
        newGoalDialog.open();
    }

    private void configureBinder() {
        binder.forField(chooseGoalType)
              .asRequired("Please select a goal type")
              .bind(ReadingGoal::getGoalType, ReadingGoal::setGoalType);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    /**
     * Vaadin's event bus system. A registered listener can be notified when a save or delete event is fired
     */
    public static abstract class GoalFormEvent extends ComponentEvent<GoalForm> {
        private ReadingGoal readingGoal;

        protected GoalFormEvent(GoalForm source, ReadingGoal readingGoal) {
            super(source, false);
            this.readingGoal = readingGoal;
        }

        public ReadingGoal getReadingGoal() {
            return readingGoal;
        }
    }

    public static class SaveEvent extends GoalFormEvent {
        SaveEvent(GoalForm source, ReadingGoal readingGoal) {
            super(source, readingGoal);
        }
    }

    public static class DeleteEvent extends GoalFormEvent {
        DeleteEvent(GoalForm source, ReadingGoal readingGoal) {
            super(source, readingGoal);
        }
    }
}
