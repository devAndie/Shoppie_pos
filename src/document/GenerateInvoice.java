package document;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import api.InvoiceData;
import singletons.Statics;
import models.SaleItem;

import com.itextpdf.text.Image;

public class GenerateInvoice {
	static String Name = Statics.getName();
	static String supplies = Statics.getDealership();
	static String address = Statics.getAddress();
	static String contact = Statics.getContact();
	static String docFolder = Statics.getInvoicesDir();
	
//	public static final String IMAGE = "src\\document\\Instalink bg.png";
	public static final String IMAGE = Statics.img;
	
	ArrayList<SaleItem> Items;
	String  CustName, CustContact, Teller, Date, InvNo, Balance, DocType, InvTotal;
		
	static ArrayList<String>  headers;
	
	com.itextpdf.text.Font hFnt, tFnt, sFnt, aFnt, ttlFnt, footFnt;
	FileOutputStream fos;
	Document doc;
	Date dateOut;
	SimpleDateFormat formatter;
	
	public GenerateInvoice(ArrayList<SaleItem> items, String custName, String cContact, String teller, String date, String invNo,
			String balance, String invTotal, String docType) {
		Items = items;
		CustName = custName;
		CustContact = cContact;
		Teller = teller;
		Date = date;
		InvNo = invNo;
		Balance = balance;
		InvTotal = invTotal;
		DocType = docType;
		
		hFnt = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 17);
		tFnt = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
		sFnt = FontFactory.getFont(FontFactory.TIMES_ITALIC, 12);
		aFnt = FontFactory.getFont(FontFactory.TIMES, 12);
		ttlFnt = FontFactory.getFont(FontFactory.TIMES_BOLD, 11);
		footFnt = FontFactory.getFont(FontFactory.HELVETICA, 10);

		hFnt.setColor(113, 194, 150);
		
		headers = new ArrayList<>();
		headers.add("SN");
		headers.add("Description");
		headers.add("Unit");
		headers.add("Quantity");
		headers.add("Price");
		headers.add("Total");
		
        float[] columnWidths = { 1f,  6f, 2.5f, 2f, 2f, 2.5f }; 
        Date dateOut = new Date();  
		SimpleDateFormat formatter = 
				new SimpleDateFormat("yyyy-MM-dd, HH:mm a");

		try  {  
			//location where PDF will be generated    
			FileOutputStream fos = new FileOutputStream(docFolder + CustName + " "+ DocType + " "+ Date +".pdf");  
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
			cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell2.setBorderColor(null);
			cell2.setLeading(15, 0);
			table.addCell(cell2);
			
			Paragraph g1 = new Paragraph();
			g1.add(new Phrase("TO : \n",tFnt));
			g1.add(new Phrase("Customer : " + CustName +"\n"));
			g1.add(new Phrase("Contact : "  + CustContact  +"\n"));
			g1.add(new Phrase("Invoiced on Date : " + Date +"\n"));
			g1.add(new Phrase("Reference Number : "+ InvNo ));
			
			PdfPCell customer = new PdfPCell(g1);
			customer.setLeading(15, 0);
			customer.setBorder(0);
			customer.setBorderColor(null);
			
			Paragraph g2 = new Paragraph();
			g2.add(new Phrase(DocType +"\n \n ", tFnt));
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
        		header.setHorizontalAlignment(Element.ALIGN_CENTER);

//            	if(i == 4 | i ==5) {
//            		header.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            	} else {
//            		header.setHorizontalAlignment(Element.ALIGN_CENTER);                		
//            	}
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
            	
            	PdfPCell sn = new PdfPCell(new Phrase(no));
	        	sn.setHorizontalAlignment(Element.ALIGN_CENTER);
	        	sn.setBorderColor(BaseColor.LIGHT_GRAY);
	        	sn.setPadding(5);;
	            itemsTable.addCell(sn);
            	
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
	            
	            PdfPCell sh = new PdfPCell(new Phrase(Sellp));
            	sh.setHorizontalAlignment(Element.ALIGN_RIGHT);
            	sh.setPaddingRight(10);
            	sh.setBorderColor(BaseColor.LIGHT_GRAY);
            	sh.setPadding(5);;
	            itemsTable.addCell(sh);
	            
	            PdfPCell ttl = new PdfPCell(new Phrase(Total));
            	ttl.setHorizontalAlignment(Element.ALIGN_RIGHT);
            	ttl.setPaddingRight(10);
            	ttl.setBorderColor(BaseColor.LIGHT_GRAY);
            	ttl.setPadding(5);
	            itemsTable.addCell(ttl);
            }
            doc.add(itemsTable);
            
		    doc.add(new Paragraph("                          "));
			
            PdfPCell inv = new PdfPCell(new Phrase("Total: KSh", ttlFnt));
            inv.setHorizontalAlignment(Element.ALIGN_RIGHT);
            inv.setBorderColor(BaseColor.LIGHT_GRAY);
            inv.setBorderWidthRight(0);
            inv.setBorderWidthLeft(0);
            inv.setPadding(5);
            
            PdfPCell invT = new PdfPCell(new Phrase(InvTotal, ttlFnt));
            invT.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invT.setBorderColor(BaseColor.LIGHT_GRAY);
//            invT.setPaddingRight(10);
            invT.setBorderWidthRight(0);
            invT.setBorderWidthLeft(0);
            invT.setPadding(5);
            
            PdfPTable ttlTable = new PdfPTable(2);
            ttlTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            ttlTable.setWidthPercentage(40);
            ttlTable.addCell(inv);
            ttlTable.addCell(invT);
            doc.add(ttlTable);
            
			doc.add(new Paragraph("                          "));
            
            PdfPCell bal = new PdfPCell(new Phrase("Invoice Balance: KSh", ttlFnt));
            bal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            bal.setBorderColor(BaseColor.LIGHT_GRAY);
            bal.setBorderWidthRight(0);
            bal.setBorderWidthLeft(0);
            bal.setBorderWidthTop(0);
            bal.setPadding(5);
            
            PdfPCell balT = new PdfPCell(new Phrase(Balance, ttlFnt));
            balT.setHorizontalAlignment(Element.ALIGN_RIGHT);
            balT.setBorderColor(BaseColor.LIGHT_GRAY);
            balT.setBorderWidthRight(0);
            balT.setBorderWidthLeft(0);
            balT.setBorderWidthTop(0);
            balT.setPadding(5);

            PdfPCell bal2 = new PdfPCell(new Phrase("Total Amount Due: KSh", ttlFnt));
            bal2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            bal2.setVerticalAlignment(Element.ALIGN_BOTTOM);
            bal2.setBorderColor(BaseColor.LIGHT_GRAY);
            bal2.setBorderWidthRight(0);
            bal2.setBorderWidthLeft(0);
            bal2.setBorderWidthTop(0);
            bal2.setPadding(5);
            
            String totalBalance = InvoiceData.getTotalBal(invNo);
            
            PdfPCell totalBal = new PdfPCell(new Phrase(totalBalance, ttlFnt));
            totalBal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalBal.setVerticalAlignment(Element.ALIGN_BOTTOM);
            totalBal.setBorderColor(BaseColor.LIGHT_GRAY);
            totalBal.setMinimumHeight(30);
            totalBal.setBorderWidthRight(0);
            totalBal.setBorderWidthLeft(0);
            totalBal.setBorderWidthTop(0);
            totalBal.setPadding(5);
            
            float[] colW= { 2f,  1f}; 

            PdfPTable balTable = new PdfPTable(2);
            balTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            balTable.setWidthPercentage(50);
            balTable.setWidths(colW);
            balTable.addCell(bal);
            balTable.addCell(balT);
            balTable.addCell(bal2);
            balTable.addCell(totalBal);
            doc.add(balTable);
            
            doc.add(new Paragraph("   "));
			doc.add(new Paragraph("You were served by : "+ Teller ));

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