package com.nolabke;

import com.nolabke.model.BudgetEntry;
import com.nolabke.model.BudgetSummary;
import com.nolabke.service.BudgetService;
import com.nolabke.service.BudgetStorage;
import com.nolabke.service.PdfExporter;
import com.nolabke.service.UpdateService;
import com.nolabke.utils.AppLogger;
import com.nolabke.utils.DateUtils;
import com.nolabke.utils.Messages;
import com.nolabke.utils.NumberParser;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class BudgetController implements Initializable {

    // Labels
    @FXML
    private Label monthLabel;

    @FXML
    private Label incomeLabel;

    @FXML
    private Label incomeValueLabel;

    @FXML
    private Label expensesLabel;

    @FXML
    private Label expensesValueLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label balanceValueLabel;

    // Buttons
    @FXML
    private Button todayButton;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    // Table
    @FXML
    private TableView<BudgetEntry> budgetTable;

    @FXML
    private TableColumn<BudgetEntry, String> dateColumn;

    @FXML
    private TableColumn<BudgetEntry, String> descriptionColumn;

    @FXML
    private TableColumn<BudgetEntry, BigDecimal> amountColumn;

    // Form
    @FXML
    private DatePicker inputDate;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField amountField;

    // Menu items
    private MenuItem copyItem;

    private YearMonth currentMonth = YearMonth.now();

    private final ObservableList<BudgetEntry> budgetEntries =
            FXCollections.observableArrayList();

    private BudgetService service;
    private PdfExporter pdfExporter;

    public BudgetController() {
        this.service = new BudgetService(new BudgetStorage());
        this.pdfExporter = new PdfExporter();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        updateLabels();
        updateMonthLabel();

        configureTable();
        configureColumns();
        configureSelection();
        configureTableContextMenu();

        budgetTable.setItems(budgetEntries);

        loadCurrentMonth();
    }

    private void updateLabels() {

        if (addButton != null) addButton.setText(Messages.get("button.add"));
        if (updateButton != null) updateButton.setText(Messages.get("button.update"));
        if (deleteButton != null) deleteButton.setText(Messages.get("button.delete"));

        if (dateColumn != null) dateColumn.setText(Messages.get("table.date"));
        if (descriptionColumn != null) descriptionColumn.setText(Messages.get("table.description"));
        if (amountColumn != null) {
            amountColumn.setText(Messages.get("table.amount"));
        }


        if (todayButton != null) {
            todayButton.setText(Messages.get("button.today"));
        }

        incomeLabel.setText(Messages.get("toolbar.income"));
        expensesLabel.setText(Messages.get("toolbar.expenses"));
        balanceLabel.setText(Messages.get("toolbar.balance"));

        if (inputDate != null) {
            inputDate.setPromptText(
                    Messages.get("placeholder.example") + " " +
                            LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            );
        }

        if (descriptionField != null) {
            descriptionField.setPromptText(
                    Messages.get("placeholder.description")
            );
        }

        if (amountField != null) {
            amountField.setPromptText(
                    Messages.get("placeholder.amount")
            );
        }
    }

    @FXML
    protected void onAdd() {

        LocalDate date = inputDate.getValue();

        if (date == null ||
                descriptionField.getText().isBlank() ||
                amountField.getText().isBlank()) {

            showAlert(
                    Messages.get("alert.error"),
                    Messages.get("alert.fillAllFields")
            );
            return;
        }


        BigDecimal amount;

        try {

            amount = NumberParser.parse(
                    amountField.getText()
            );

        } catch (NumberFormatException e) {

            AppLogger.warning(
                    "Invalid amount format entered by user"
            );
            showAlert(
                    Messages.get("alert.error"),
                    Messages.get("alert.invalidAmount")
            );

            return;
        }


        BudgetEntry entry =
                new BudgetEntry(
                        date,
                        descriptionField.getText(),
                        amount
                );


        service.add(entry);


        if (YearMonth.from(date).equals(currentMonth)) {
            loadCurrentMonth();
        }


        clearFields();


        showAlert(
                Messages.get("alert.info"),
                Messages.get("alert.entryAdded")
        );
    }

    @FXML
    protected void onUpdate() {

        BudgetEntry selected =
                budgetTable.getSelectionModel()
                        .getSelectedItem();


        if (selected == null) {

            showAlert(
                    Messages.get("alert.info"),
                    Messages.get("alert.selectEntryToEdit")
            );

            return;
        }


        BudgetEntry edited =
                new BudgetEntry(
                        selected.getId(),
                        inputDate.getValue(),
                        descriptionField.getText(),
                        NumberParser.parse(
                                amountField.getText())
                );


        service.update(edited);


        loadCurrentMonth();

        clearFields();

        budgetTable.getSelectionModel()
                .clearSelection();
    }


    @FXML
    protected void onDelete() {

        BudgetEntry selected =
                budgetTable.getSelectionModel()
                        .getSelectedItem();


        if (selected == null) {

            showAlert(
                    Messages.get("alert.info"),
                    Messages.get("alert.selectEntryToDelete")
            );

            return;
        }


        service.delete(selected);


        loadCurrentMonth();


        clearFields();


        budgetTable.getSelectionModel()
                .clearSelection();
    }

    @FXML
    protected void previousMonth() {

        currentMonth = currentMonth.minusMonths(1);

        updateMonthLabel();

        loadCurrentMonth();

        todayButton.setDisable(
                currentMonth.equals(YearMonth.now())
        );
    }

    @FXML
    protected void nextMonth() {

        currentMonth = currentMonth.plusMonths(1);

        updateMonthLabel();

        loadCurrentMonth();

        todayButton.setDisable(
                currentMonth.equals(YearMonth.now())
        );
    }

    private void updateMonthLabel() {

        String month = currentMonth
                .getMonth()
                .getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Messages.getCurrentLocale()
                );

        String formattedMonth =
                month.substring(0, 1)
                        .toUpperCase(Messages.getCurrentLocale())
                        + month.substring(1);

        monthLabel.setText(
                formattedMonth + " " + currentMonth.getYear()
        );
    }

    private void loadCurrentMonth() {

        budgetEntries.setAll(
                service.getEntries(currentMonth)
        );

        updateSummaryLabels();
        //budgetTable.refresh();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {

        inputDate.setValue(null);

        descriptionField.clear();

        amountField.clear();
    }

    public void refreshLanguage() {
        updateLabels();
        updateMonthLabel();

        copyItem.setText(Messages.get("menu.copy"));
        configureAmountColumn();
        updateSummaryLabels();
        budgetTable.refresh();
    }


    public void export(Stage owner) {
        FileChooser chooser = pdfExporter.createFileChooser(currentMonth);

        File file = chooser.showSaveDialog(owner);

        if (file == null) {
            return;
        }

        pdfExporter.exportBudget(
                budgetEntries,
                currentMonth,
                file
        );

        showAlert(
                Messages.get("alert.info"),
                Messages.get("alert.export.success")
        );
    }

    @FXML
    protected void goToCurrentMonth() {

        currentMonth = YearMonth.now();

        updateMonthLabel();

        loadCurrentMonth();
    }

    private void updateSummaryLabels() {

        BudgetSummary summary =
                service.calculateBudgetSummary(budgetEntries);

        incomeValueLabel.setText(formatAmount(summary.getIncome()));
        expensesValueLabel.setText(formatAmount(summary.getExpenses()));
        balanceValueLabel.setText(formatAmount(summary.getBalance()));
    }

    private void configureTable() {

        budgetTable.setPlaceholder(new Label());

        budgetTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        dateColumn.prefWidthProperty()
                .bind(budgetTable.widthProperty().multiply(0.20));

        descriptionColumn.prefWidthProperty()
                .bind(budgetTable.widthProperty().multiply(0.60));

        amountColumn.prefWidthProperty()
                .bind(budgetTable.widthProperty().multiply(0.20));
    }

    private String formatAmount(BigDecimal value) {

        NumberFormat formatter = createNumberFormatter();

        String formatted = formatter.format(value.abs());

        if (value.signum() < 0) {
            return "- " + formatted;
        }

        return formatted;
    }

    private void configureColumns() {

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        DateUtils.formatDate(
                                cellData.getValue().getDate()
                        )
                )
        );

        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getDescription()
                )
        );

        amountColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(
                        cellData.getValue().getAmount()
                )
        );

        configureAmountColumn();
    }

    private void configureAmountColumn() {

        amountColumn.setCellFactory(column -> {

            TableCell<BudgetEntry, BigDecimal> cell =
                    new TableCell<>() {

                        @Override
                        protected void updateItem(
                                BigDecimal item,
                                boolean empty
                        ) {

                            super.updateItem(item, empty);

                            getStyleClass().removeAll(
                                    "positive-amount",
                                    "negative-amount"
                            );

                            if (empty || item == null) {
                                setText(null);
                                return;
                            }

                            if (item.signum() < 0) {
                                getStyleClass().add("negative-amount");
                            }

                            setText(formatAmount(item));
                        }
                    };

            cell.getStyleClass().add("amount-cell");

            return cell;
        });
    }

    private void configureSelection() {

        budgetTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, entry) -> {

                    if (entry == null) {
                        return;
                    }

                    inputDate.setValue(entry.getDate());
                    descriptionField.setText(entry.getDescription());
                    amountField.setText(
                            formatAmount(entry.getAmount())
                    );
                });
    }

    private void configureTableContextMenu() {

        copyItem = new MenuItem(Messages.get("menu.copy"));

        copyItem.setOnAction(event -> {

            StringBuilder clipboardString = new StringBuilder();

            for (BudgetEntry entry : budgetTable.getSelectionModel().getSelectedItems()) {

                clipboardString
                        .append(DateUtils.formatDate(entry.getDate()))
                        .append("\t")
                        .append(entry.getDescription())
                        .append("\t")
                        .append(entry.getAmount())
                        .append(System.lineSeparator());
            }

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            content.putString(clipboardString.toString());
            clipboard.setContent(content);
        });


        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(copyItem);

        budgetTable.setContextMenu(contextMenu);
    }

    private NumberFormat createNumberFormatter() {

        Locale locale = Messages.getCurrentLocale();

        if (locale.getLanguage().equals("pl")) {

            DecimalFormatSymbols symbols =
                    DecimalFormatSymbols.getInstance(locale);

            symbols.setGroupingSeparator('.');

            DecimalFormat formatter =
                    new DecimalFormat("#,##0.00", symbols);

            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);

            return formatter;
        }


        NumberFormat formatter =
                NumberFormat.getNumberInstance(locale);

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter;
    }
}
