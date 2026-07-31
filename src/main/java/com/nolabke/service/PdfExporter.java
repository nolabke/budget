package com.nolabke.service;

import com.nolabke.model.BudgetEntry;
import com.nolabke.utils.AppLogger;
import com.nolabke.utils.DateUtils;
import com.nolabke.utils.Messages;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import com.nolabke.model.BudgetSummary;


public class PdfExporter {

    public void exportBudget(
            ObservableList<BudgetEntry> budget,
            YearMonth month,
            File file
    ) {

        float pageWidth = PDRectangle.A4.getWidth();
        float pageHeight = PDRectangle.A4.getHeight();

        float margin = 50;

        float titleSpacing = 30;

        float tableWidth = pageWidth - (2 * margin);

        float dateWidth = tableWidth * 0.20f;
        float descriptionWidth = tableWidth * 0.50f;
        float amountWidth = tableWidth * 0.30f;

        float y = pageHeight - margin- titleSpacing;


        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(
                    PDRectangle.A4
            );

            document.addPage(page);


            PDPageContentStream content =
                    new PDPageContentStream(
                            document,
                            page
                    );


            InputStream fontRegular =
                    getClass().getResourceAsStream(
                            "/fonts/NotoSans-Regular.ttf"
                    );

            PDFont font =
                    PDType0Font.load(
                            document,
                            fontRegular
                    );

            InputStream fontBold =
                    getClass().getResourceAsStream(
                            "/fonts/NotoSans-Bold.ttf"
                    );

            PDFont bold =
                    PDType0Font.load(
                            document,
                            fontBold
                    );

            // HEADER

            content.beginText();

            content.setFont(
                    bold,
                    24
            );

            content.newLineAtOffset(
                    margin,
                    y
            );

            String title =
                    Messages.get("pdf.budget.title");

            content.showText(title);
            content.endText();
            y -= 25;

            String monthName = month
                    .getMonth()
                    .getDisplayName(
                            TextStyle.FULL_STANDALONE,
                            Messages.getCurrentLocale()
                    );

            String period =
                    monthName.substring(0, 1).toUpperCase()
                            + monthName.substring(1)
                            + " "
                            + month.getYear();


            content.beginText();

            content.setFont(
                    font,
                    16
            );

            content.newLineAtOffset(
                    margin,
                    y
            );

            content.showText(period);

            content.endText();


            y -= 80;


            // TABLE HEADER

            drawCell(
                    content,
                    Messages.get("table.date"),
                    margin,
                    y,
                    dateWidth,
                    bold
            );


            drawCell(
                    content,
                    Messages.get("table.description"),
                    margin + dateWidth,
                    y,
                    descriptionWidth,
                    bold
            );


            drawRightAlignedCell(
                    content,
                    Messages.get("table.amount"),
                    margin + dateWidth + descriptionWidth,
                    y,
                    amountWidth,
                    bold
            );

            // LINE UNDER TABLE HEADER

            float headerLineSpacing = 10;
            float lineToRowsSpacing = 30;

            content.setLineWidth(0.5f);
            content.moveTo(
                    margin,
                    y - headerLineSpacing
            );

            content.lineTo(
                    margin + tableWidth,
                    y - headerLineSpacing
            );

            content.stroke();

            y = y - headerLineSpacing - lineToRowsSpacing;



            // ROWS

            for (BudgetEntry b : budget) {


                float rowHeight =
                        Math.max(
                                20,
                                calculateTextHeight(
                                        b.getDescription(),
                                        descriptionWidth,
                                        font,
                                        10
                                )
                        );


                drawCell(
                        content,
                        DateUtils.formatDate(b.getDate()),
                        margin,
                        y,
                        dateWidth,
                        font
                );


                drawWrappedCell(
                        content,
                        b.getDescription(),
                        margin + dateWidth,
                        y,
                        descriptionWidth,
                        font
                );


                drawRightAlignedCell(
                        content,
                        formatAmount(b.getAmount()),
                        margin + dateWidth + descriptionWidth,
                        y,
                        amountWidth,
                        font
                );


                y -= rowHeight + 5;


                if (y < margin) {

                    content.close();

                    page = new PDPage(
                            PDRectangle.A4
                    );

                    document.addPage(page);

                    content =
                            new PDPageContentStream(
                                    document,
                                    page
                            );

                    y = pageHeight - margin;
                }
            }

            drawFooter(
                    content,
                    budget,
                    pageWidth,
                    margin,
                    font,
                    pageHeight
            );

            content.close();

            document.save(file);

        } catch(Exception e) {

            AppLogger.getLogger()
                    .log(
                            Level.SEVERE,
                            "PDF export failed",
                            e
                    );
        }
    }

    private void drawCell(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            float width,
            PDFont font
    ) throws IOException {


        content.beginText();

        content.setFont(
                font,
                14
        );

        content.newLineAtOffset(
                x,
                y
        );

        content.showText(
                text
        );

        content.endText();
    }

    private void drawWrappedCell(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            float width,
            PDFont font
    ) throws IOException {


        List<String> lines =
                wrapText(
                        text,
                        font,
                        14,
                        width
                );


        float offset = 0;


        for (String line : lines) {

            content.beginText();

            content.setFont(
                    font,
                    14
            );

            content.newLineAtOffset(
                    x,
                    y - offset
            );

            content.showText(
                    line
            );

            content.endText();

            offset -= 12;
        }
    }
        private List<String> wrapText(
                String text,
                PDFont font,
                float fontSize,
                float width ) throws IOException {


            List<String> lines =
                    new ArrayList<>();

            StringBuilder current =
                    new StringBuilder();


            for(String word : text.split(" ")) {


                String test =
                        current.length() == 0
                                ? word
                                : current + " " + word;


                float size =
                        font.getStringWidth(test)
                                / 1000
                                * fontSize;


                if(size > width) {

                    lines.add(
                            current.toString()
                    );

                    current =
                            new StringBuilder(word);

                } else {

                    current =
                            new StringBuilder(test);
                }
            }


            if(!current.isEmpty()) {

                lines.add(
                        current.toString()
                );
            }


            return lines;
        }

    private float calculateTextHeight(
            String text,
            float width,
            PDFont font,
            float fontSize) throws IOException {

        List<String> lines =
                wrapText(
                        text,
                        font,
                        fontSize,
                        width
                );

        float lineHeight = fontSize + 2;

        return lines.size() * lineHeight;
    }

    private void drawRightAlignedCell(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            float width,
            PDFont font
    ) throws IOException {

        float fontSize = 14;

        float textWidth =
                font.getStringWidth(text)
                        / 1000
                        * fontSize;

        float textX =
                x + width - textWidth -5;

        content.beginText();

        content.setFont(
                font,
                fontSize
        );

        content.newLineAtOffset(
                textX,
                y
        );

        content.showText(
                text
        );

        content.endText();
    }

    public String generateFileName(YearMonth month) {

        String monthName = month
                .getMonth()
                .getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Messages.getCurrentLocale()
                )
                .toLowerCase();

        monthName = Normalizer
                .normalize(monthName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return "budget_" + monthName + "_" + month.getYear() + ".pdf";
    }

    public FileChooser createFileChooser(YearMonth month) {

        FileChooser chooser = new FileChooser();

        chooser.setTitle(Messages.get("menu.export"));

        chooser.setInitialFileName(generateFileName(month));

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "PDF files",
                        "*.pdf"
                )
        );

        return chooser;
    }

    private String formatAmount(BigDecimal value) {

        Locale locale = Messages.getCurrentLocale();

        NumberFormat formatter;

        if (locale.getLanguage().equals("pl")) {

            DecimalFormatSymbols symbols =
                    DecimalFormatSymbols.getInstance(locale);

            symbols.setGroupingSeparator('.');

            formatter = new DecimalFormat("#,##0.00", symbols);

        } else {

            formatter = NumberFormat.getNumberInstance(locale);
        }

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);


        String formatted =
                formatter.format(value.abs());


        if (value.signum() < 0) {
            return "- " + formatted;
        }

        return formatted;
    }

    private void drawFooter(
            PDPageContentStream content,
            ObservableList<BudgetEntry> budget,
            float pageWidth,
            float margin,
            PDFont font,
            float pageHeight
    ) throws IOException {

        float footerY = margin;
        content.setLineWidth(0.5f);

        content.moveTo(
                margin,
                footerY + 25
        );

        content.lineTo(
                pageWidth - margin,
                footerY + 25
        );

        content.stroke();



        // DATE LEFT

        String date =
                Messages.get("pdf.created")
                        + ": "
                        + LocalDate.now()
                        .format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        );


        drawFooterText(
                content,
                date,
                margin,
                footerY,
                font
        );

        // SUMMARY RIGHT

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;


        for (BudgetEntry entry : budget) {

            if (entry.getAmount().signum() >= 0) {
                income = income.add(entry.getAmount());
            } else {
                expenses = expenses.add(
                        entry.getAmount().abs()
                );
            }
        }


        BigDecimal balance =
                income.subtract(expenses);



        String summary =
                Messages.get("toolbar.income")
                        + ": "
                        + formatAmount(income)
                        + "    "
                        + Messages.get("toolbar.expenses")
                        + ": "
                        + formatAmount(expenses)
                        + "    "
                        + Messages.get("toolbar.balance")
                        + ": "
                        + formatAmount(balance);



        float fontSize = 10;

        float textWidth =
                font.getStringWidth(summary)
                        / 1000
                        * fontSize;


        content.beginText();

        content.setFont(
                font,
                fontSize
        );

        content.newLineAtOffset(
                pageWidth - margin - textWidth,
                footerY
        );

        content.showText(summary);

        content.endText();
    }

    private void drawFooterText(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            PDFont font
    ) throws IOException {

        content.beginText();

        content.setFont(
                font,
                10
        );

        content.newLineAtOffset(
                x,
                y
        );

        content.showText(text);

        content.endText();
    }
}