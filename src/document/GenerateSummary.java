package document;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import singletons.Statics;

public class GenerateSummary {
	static String Name = Statics.getName();
	static String supplies = Statics.getDealership();
	static String address = Statics.getAddress();
	static String contact = Statics.getContact();
	static String docFolder = Statics.getDeliveryDir();
	public static final String IMAGE = Statics.img;
		
	com.itextpdf.text.Font hFnt, sFnt, aFnt, ttlFnt, footFnt;
	FileOutputStream fos;
	Document doc;
	Date dateOut;
	SimpleDateFormat formatter;
	
	String Date, Customers, Quotes, Invoices, Receipts, PMoney, CMoney, TMoney;
	String DocType;
	
	public GenerateSummary(String date, String customers, String quotes, String invoices, String receipts,
			String pMoney, String cMoney, String tMoney) {
		Date = date;
		Customers = customers;
		Quotes = quotes;
		Invoices = invoices;
		Receipts = receipts;
		PMoney = pMoney;
		CMoney = cMoney;
		TMoney = tMoney;
		
		DocType = "Summary";
		
		hFnt = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 17);
		sFnt = FontFactory.getFont(FontFactory.TIMES_ITALIC, 12);
		aFnt = FontFactory.getFont(FontFactory.TIMES, 12);
		ttlFnt = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
		footFnt = FontFactory.getFont(FontFactory.HELVETICA, 10);
		hFnt.setColor(113, 194, 150);
		
        dateOut = new Date();  
		formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm a");
		
		
		try  {  
			//location where PDF will be generated    
			FileOutputStream fos = new FileOutputStream(docFolder + Date+ " "+DocType + ".pdf");  

			Document doc = new Document(PageSize.A4, 30f, 30f, 20f, 20f);
			
			//doc writer for the PDF  
			PdfWriter writer = PdfWriter.getInstance(doc, fos);  
			
			ParagraphBorder border = new ParagraphBorder();
		    writer.setPageEvent(border);
		    
			//opens the PDF  
			doc.open();  		
			doc.addTitle(Date+ " "+DocType);
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
			g1.add(new Phrase("\n \n \n Summary for: " + Date ));
			
			PdfPCell title = new PdfPCell(g1);
			title.setVerticalAlignment(Element.ALIGN_BOTTOM);
			title.setLeading(15, 0);
			title.setBorder(0);
			title.setBorderColor(null);
			
			Paragraph g2 = new Paragraph();
			g2.add(new Phrase("\n\n "+ DocType +"\n \n ", ttlFnt));
			g2.add("Printed on : " + formatter.format(dateOut));			
			PdfPCell type = new PdfPCell(g2);
			type.setHorizontalAlignment(Element.ALIGN_RIGHT);
			type.setLeading(15, 0);
			type.setBorder(0);
			type.setBorderColor(null);
			

			Paragraph cst = new Paragraph();
			cst.add(new Phrase("\n Customers \n", ttlFnt));
			cst.add(Customers +"\n ");			
			PdfPCell cust = new PdfPCell(cst);
			cust.setHorizontalAlignment(Element.ALIGN_CENTER);
			cust.setLeading(15, 0);
			cust.setBorder(0);
			cust.setBorderColor(null);
			
			Paragraph q = new Paragraph();
			q.add(new Phrase("Quotes \n ", ttlFnt));
			q.add(Quotes +"\n ");			
			PdfPCell qts = new PdfPCell(q);
			qts.setHorizontalAlignment(Element.ALIGN_CENTER);
			qts.setLeading(15, 0);
			qts.setBorder(0);
			qts.setBorderColor(null);

			Paragraph inv = new Paragraph();
			inv.add(new Phrase("Invoices \n ", ttlFnt));
			inv.add(Invoices +"\n ");			
			PdfPCell invoice = new PdfPCell(inv);
			invoice.setHorizontalAlignment(Element.ALIGN_CENTER);
			invoice.setLeading(15, 0);
			invoice.setBorder(0);
			invoice.setBorderColor(null);
			
			Paragraph re = new Paragraph();
			re.add(new Phrase("Receipts \n ", ttlFnt));
			re.add(Receipts +"\n ");			
			PdfPCell recpt= new PdfPCell(re);
			recpt.setHorizontalAlignment(Element.ALIGN_CENTER);
			recpt.setLeading(15, 0);
			recpt.setBorder(0);
			recpt.setBorderColor(null);

			Paragraph pm = new Paragraph();
			pm.add(new Phrase("M-Pesa \n ", ttlFnt));
			pm.add(PMoney +"\n ");			
			PdfPCell pmny = new PdfPCell(pm);
			pmny.setHorizontalAlignment(Element.ALIGN_CENTER);
			pmny.setLeading(15, 0);
			pmny.setBorder(0);
			pmny.setBorderColor(null);

			Paragraph cm = new Paragraph();
			cm.add(new Phrase("Cash \n ", ttlFnt));
			cm.add(CMoney +"\n ");			
			PdfPCell csh = new PdfPCell(cm);
			csh.setHorizontalAlignment(Element.ALIGN_CENTER);
			csh.setLeading(15, 0);
			csh.setBorder(0);
			csh.setBorderColor(null);

			Paragraph tm = new Paragraph();
			tm.add(new Phrase("Total \n ", ttlFnt));
			tm.add(TMoney +"\n ");			
			PdfPCell tsh = new PdfPCell(tm);
			tsh.setHorizontalAlignment(Element.ALIGN_CENTER);
			tsh.setLeading(15, 0);
			tsh.setBorder(0);
			tsh.setBorderColor(null);

			PdfPCell f = new PdfPCell();
			f.setBorder(0);
			f.setBorderColor(null);
			
			table.addCell(title);
			table.addCell(type);
			
			table.addCell(cust);
			table.addCell(f);

			table.addCell(qts);			table.addCell(pmny);
			table.addCell(invoice);		table.addCell(csh);
			table.addCell(recpt);			table.addCell(tsh);
			
			doc.add(table);
		    
		    
		    
		    
            doc.add(new Paragraph("\n\n\n\n\n\n\n"));
            
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
