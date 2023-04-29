 package document;

import java.awt.Color;
import java.awt.Font;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.text.StyleConstants.FontConstants;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import singletons.Statics;
import models.SaleItem;

import com.itextpdf.text.Image;

public class GenerateDelivery {
	static String Name = Statics.getName();
	static String supplies = Statics.getDealership();
	static String address = Statics.getAddress();
	static String contact = Statics.getContact();
	static String docFolder = Statics.getDeliveryDir();
	
//	public static final String IMAGE = "src\\document\\Instalink bg.png";
	public static final String IMAGE = Statics.img;
	
	ArrayList<SaleItem> Items;
	String  CustName, CustContact, Date, InvNo, DocType;
	
	static ArrayList<String>  headers;
	
	com.itextpdf.text.Font hFnt, sFnt, aFnt, ttlFnt, footFnt;
	FileOutputStream fos;
	Document doc;
	Date dateOut;
	SimpleDateFormat formatter;
	
	public GenerateDelivery(ArrayList<SaleItem> items, String custName, String cContact, String date, 
			String invNo, String docType) {
		Items = items;
		CustName = custName;
		CustContact = cContact;
		Date = date;
		InvNo = invNo;
		DocType = docType;
		
		hFnt = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 17);
		sFnt = FontFactory.getFont(FontFactory.TIMES_ITALIC, 12);
		aFnt = FontFactory.getFont(FontFactory.TIMES, 12);
		ttlFnt = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
		footFnt = FontFactory.getFont(FontFactory.HELVETICA, 10);
		hFnt.setColor(113, 194, 150);
		
		headers = new ArrayList<>();
		headers.add("Description");
		headers.add("Unit");
		headers.add("Quantity");
		
        float[] columnWidths = { 6f, 1f, 1.5f}; 
        dateOut = new Date();  
		formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm a");

		try  {  
			//location where PDF will be generated    
			FileOutputStream fos = new FileOutputStream(docFolder +CustName+ " "+DocType + " "+ Date+".pdf");  
//			System.out.println("File Generated.");  
			//creates an instance of PDF document  
			Document doc = new Document(PageSize.A4, 30f, 30f, 20f, 20f);
			
			//doc writer for the PDF  
			PdfWriter writer = PdfWriter.getInstance(doc, fos);  
			
//			Rectangle rect = new Rectangle(20, 20, 550, 800);
//	        writer.setBoxSize("art", rect);
			ParagraphBorder border = new ParagraphBorder();
		    writer.setPageEvent(border);
		    
			//opens the PDF  
			doc.open();  		
			doc.addTitle(CustName+ " "+DocType + " "+ Date);
			PdfContentByte canvas = writer.getDirectContentUnder();
	        Image image = Image.getInstance(IMAGE);
	        image.scaleAbsolute(160, 100);
	        image.setAbsolutePosition(210F, 675F);
	        canvas.saveState();
	        PdfGState state = new PdfGState();
	        state.setFillOpacity(0.7f);
	        canvas.setGState(state);
	        canvas.addImage(image);
	        canvas.restoreState();
			
	        //k.setTextAlignment(TextAlignment.RIGHT);
			
			//adding paragraphs to the PDF  
			doc.add(new Paragraph("                                  "+ Name, hFnt));
//			doc.add(new Paragraph("                          "));

			//Letter head
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100f);
			table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);
            
			PdfPCell cell1 = new PdfPCell(new Phrase(supplies, sFnt));
			cell1.setBorder(0);
			cell1.setBorderColor(null);
			cell1.setLeading(15, 0);
			table.addCell(cell1);
			
			
			Phrase addr = new Phrase(address + contact, aFnt);
//			Phrase cntct = new Phrase(contact, aFnt);
			PdfPCell cell2 = new PdfPCell(addr);
			cell2.setBorder(0);
			cell2.setBorderColor(null);
			cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell2.setLeading(15, 0);
			table.addCell(cell2);
			

			Paragraph g1 = new Paragraph();
			g1.add(new Phrase("TO : \n",ttlFnt));
			g1.add(new Phrase("Customer : " + CustName +"\n"));
			g1.add(new Phrase("Contact : "  + CustContact  +"\n"));
			g1.add(new Phrase("Invoiced on Date : " + Date +"\n"));
			g1.add(new Phrase("Reference Invoice Number : "+ InvNo ));
			
			PdfPCell customer = new PdfPCell(g1);
			customer.setLeading(15, 0);
			customer.setBorder(0);
			customer.setBorderColor(null);
			
			Paragraph g2 = new Paragraph();
			g2.add(new Phrase(DocType +"\n \n ", ttlFnt));
			g2.add("Printed on : " + formatter.format(dateOut));
		
			
			PdfPCell type = new PdfPCell(g2);
			type.setVerticalAlignment(Element.ALIGN_BOTTOM);
			type.setHorizontalAlignment(Element.ALIGN_RIGHT);
			type.setLeading(15, 0);
			type.setBorder(0);
			type.setBorderColor(null);
			
			
			table.addCell(customer);
			table.addCell(type);
		    doc.add(table);
		      
//		    doc.add(new Paragraph("                          "));
			
		    //Items table
			PdfPTable itemsTable = new PdfPTable(headers.size());
			itemsTable.setWidthPercentage(100f);
			 // Set Column widths of table
            itemsTable.setWidths(columnWidths);
            itemsTable.setSpacingBefore(10f);
            itemsTable.setSpacingAfter(5f);

            //adding table headers
            for (int i = 0; i < headers.size(); i++) {
            	PdfPCell header = new PdfPCell(new 
            				Phrase(headers.get(i).toString())
            			);
            	if(i == 4 | i ==5) {
            		header.setHorizontalAlignment(Element.ALIGN_RIGHT);
                           	
            	} else {
            		header.setHorizontalAlignment(Element.ALIGN_CENTER);
                		
            	}
            	header.setPadding(5);
            	header.setBorderColor(BaseColor.LIGHT_GRAY);
                itemsTable.addCell(header);
                

            }

            int SN =0;
            String no, prodName, Desc, uom, Dimens, Quantity, Sellp, Total;
            String particulars;
//			  extracting data from the JTable and inserting it to PdfPTable
            for (int i = 0; i < Items.size(); i++) {
            	SN = SN +1;
            	no = String.valueOf(SN);
            	prodName = Items.get(i).getName();
            	Desc = Items.get(i).getDesc();
            	uom = Items.get(i).getUnit();
            	Dimens = Items.get(i).getDimens();
            	Quantity = Items.get(i).getQuantity();
            	Sellp = Items.get(i).getSellp();
            	Total = Items.get(i).getTotal();
            	
            	if(Dimens == null) {
            		particulars = prodName;
            		
            	} else {
            		particulars = prodName +" "+Dimens;
            	}
	            
	            PdfPCell prod = new PdfPCell(new Phrase(particulars));
	            prod.setBorderColor(BaseColor.LIGHT_GRAY);
	            prod.setPadding(5);
	            itemsTable.addCell(prod);
	    		
            	PdfPCell unit = new PdfPCell(new Phrase(uom));
            	unit.setHorizontalAlignment(Element.ALIGN_CENTER);
            	unit.setBorderColor(BaseColor.LIGHT_GRAY);
            	unit.setPadding(5);
	            itemsTable.addCell(unit);
	    		
	            
	            PdfPCell qty = new PdfPCell(new Phrase(Quantity));
	            qty.setHorizontalAlignment(Element.ALIGN_CENTER);
	            qty.setBorderColor(BaseColor.LIGHT_GRAY);
	            qty.setPadding(5);;
	            itemsTable.addCell(qty);
	            
            }
            doc.add(itemsTable);
            
		    doc.add(new Paragraph("     \n \n     "));
		    doc.add(new Paragraph("Thank for your order."));
			
		    doc.add(new Paragraph("Please check all items carefully and notify us of any "
		    		+ "discrepancy within 24 hours \n "));
//		    doc.add(new Paragraph("                          "));
			doc.add(new Paragraph("Items recieved by: \n "));
//			doc.add(new Paragraph("                          "));
			
			doc.add(new Paragraph("Signature:__________________    Name:________________________   \n "));
//			doc.add(new Paragraph("                          "));
			doc.add(new Paragraph("Date:___________________"));
			
            
            doc.add(new Paragraph("\n\n\n"));
            
            Paragraph acc = new Paragraph("System Developed by Andru Developers \n"
            		+ "website : www.andrudevlab.com", footFnt);
            acc.setAlignment(Element.ALIGN_CENTER);
            
            doc.add(acc);
                        
			//closes the document  
			doc.close();  
			//closes the output stream  
			fos.close();  
		} 
		//catch the exception if any   
		catch (Exception e) {  
			//prints the occurred exception   
			e.printStackTrace();  
		}  

		
	}    



}