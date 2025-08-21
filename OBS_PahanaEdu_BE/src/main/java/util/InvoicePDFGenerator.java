package util;

//import com.itextpdf.io.font.constants.StandardFonts;
//import com.itextpdf.kernel.colors.ColorConstants;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.*;
//import com.itextpdf.layout.properties.TextAlignment;
//import model.Bill;
//import model.OrderItem;
//
//import java.io.ByteArrayOutputStream;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.List;
//
//public class InvoicePDFGenerator {
//
//    public static byte[] generateInvoicePDF(Bill bill) throws Exception {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter writer = new PdfWriter(baos);
//        PdfDocument pdfDoc = new PdfDocument(writer);
//        Document document = new Document(pdfDoc);
//
//        // Title
//        Paragraph title = new Paragraph("PahanaEdu Bookshop")
//                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
//                .setFontSize(20)
//                .setFontColor(ColorConstants.BLUE)
//                .setTextAlignment(TextAlignment.CENTER);
//        document.add(title);
//
//        document.add(new Paragraph("Invoice")
//                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
//                .setFontSize(16)
//                .setTextAlignment(TextAlignment.CENTER)
//                .setMarginBottom(20));
//
//        // Bill info
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Table infoTable = new Table(new float[]{150, 250}); // column widths
//        infoTable.setWidthPercent(100);
//
//        infoTable.addCell(new Cell().add(new Paragraph("Invoice ID:")));
//        infoTable.addCell(new Cell().add(new Paragraph(String.valueOf(bill.getBillID()))));
//
//        infoTable.addCell(new Cell().add(new Paragraph("Bill Date:")));
//        infoTable.addCell(new Cell().add(new Paragraph(
//                bill.getBillDateStr() != null ? sdf.format(java.sql.Date.valueOf(bill.getBillDateStr())) : "N/A"
//        )));
//
//        infoTable.addCell(new Cell().add(new Paragraph("Customer ID:")));
//        infoTable.addCell(new Cell().add(new Paragraph(
//                String.valueOf(bill.getCustomerID()) // primitives can't be null
//        )));
//
//        infoTable.addCell(new Cell().add(new Paragraph("Total:")));
//        infoTable.addCell(new Cell().add(new Paragraph(String.format("%.2f LKR", bill.getTotalAmount()))));
//
//        infoTable.addCell(new Cell().add(new Paragraph("Discount:")));
//        infoTable.addCell(new Cell().add(new Paragraph(String.format("%.2f %%", bill.getDiscount()))));
//
//        infoTable.addCell(new Cell().add(new Paragraph("Final Amount:")));
//        infoTable.addCell(new Cell().add(new Paragraph(String.format("%.2f LKR", bill.getFinalAmount()))));
//
//        document.add(infoTable);
//        document.add(new Paragraph("\n"));
//
//        // Order items
//        Table table = new Table(new float[]{50, 200, 50, 80, 80}); // column widths
//        table.setWidthPercent(100);
//
//        // Header
//        table.addHeaderCell(new Cell().add(new Paragraph("Item ID").setBold()));
//        table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
//        table.addHeaderCell(new Cell().add(new Paragraph("Qty").setBold()));
//        table.addHeaderCell(new Cell().add(new Paragraph("Unit Price").setBold()));
//        table.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setBold()));
//
//        List<OrderItem> items = bill.getOrderItems();
//        if (items != null) {
//            for (OrderItem item : items) {
//                table.addCell(String.valueOf(item.getItemID()));
//                table.addCell(item.getDescription());
//                table.addCell(String.valueOf(item.getQuantity()));
//                table.addCell(String.format("%.2f LKR", item.getUnitPrice()));
//
//                BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
//                table.addCell(String.format("%.2f LKR", subtotal));
//            }
//        }
//
//        document.add(table);
//
//        document.add(new Paragraph("\nThank you for your purchase!").setTextAlignment(TextAlignment.CENTER));
//
//        document.close();
//        return baos.toByteArray();
//    }
//}
