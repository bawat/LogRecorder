package LogRecorder;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import lombok.Data;
import lombok.experimental.ExtensionMethod;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ExtensionMethod({java.util.Arrays.class, PDFGenerator.Extensions.class})
public class PDFGenerator {
	
	static class Extensions {
		public static Date toDay(LocalDate in) {
			return Date.from(in.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
	}
	
	public static File generateInvoice(LocalDate date)
	{
		JasperReport jasperReport;
	    JasperPrint jasperPrint;
	    
	    YamlFile config = YamlFile.load();
	    String invoiceNumber = new File(config.invoiceDestinationFolder).getName().toUpperCase();
    	DecimalFormat formatter = new DecimalFormat("0000");
    	
	    int number = 0;
	    File file;
	    do {
		    file = new File(config.invoiceDestinationFolder + "\\" + invoiceNumber + formatter.format(++number) + ".pdf");
	    } while(file.exists());
	    invoiceNumber += formatter.format(number);
	    
	    ArrayList<InvoiceBean> beans = new ArrayList<InvoiceBean>();
	
	    	InvoiceBean fullHoursBean = new InvoiceBean();
		    	fullHoursBean.setHoursWorked(160);
		    	fullHoursBean.setInvoiceDate(date.toDay());
		    	fullHoursBean.setDueDate(LocalDate.now().plusDays(30).toDay());
		    	fullHoursBean.setInvoiceNumber(invoiceNumber);
		    	fullHoursBean.setStartingDateInclusive(date.minusDays(30).toDay());
		    	fullHoursBean.setEndingDateInclusive(date.minusDays(1).toDay());
	    
		    InvoiceBean halfHoursBean = new InvoiceBean();
			    halfHoursBean.setHoursWorked(80);
			    halfHoursBean.setInvoiceDate(date.toDay());
			    halfHoursBean.setDueDate(LocalDate.now().plusDays(30).toDay());
			    halfHoursBean.setInvoiceNumber(invoiceNumber);
			    halfHoursBean.setStartingDateInclusive(date.minusDays(30).toDay());
			    halfHoursBean.setEndingDateInclusive(date.minusDays(1).toDay());
			    
		    InvoiceBean bean = halfHoursBean;
		    beans.add(bean);
			    
	    final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(beans);
	    
	    try
	    {
	      jasperReport = JasperCompileManager.compileReport(config.invoiceTemplateFile);
	      jasperPrint = JasperFillManager.fillReport(jasperReport, bean.linkParameters(), dataSource);
	      JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
	    }
	    catch (JRException e)
	    {
	      e.printStackTrace();
	    }
	    
	    return file;
	}
	
	public static void main(String[] args) {
		File invoice = generateInvoice(LocalDate.of(2021, 10, 27));
		try {
			Desktop.getDesktop().open(invoice);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Data static class InvoiceBean{
		private int hoursWorked;
		private Date invoiceDate;
		private Date dueDate;
		private String invoiceNumber;
		private Date startingDateInclusive;
		private Date endingDateInclusive;
		
		//Tells JasperSoft which variables contain which parameters
		public HashMap<String, Object> linkParameters()
		{
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			
			hashMap.put("HoursWorked", hoursWorked);
			hashMap.put("InvoiceDate", invoiceDate);
			hashMap.put("DueDate", dueDate);
			hashMap.put("InvoiceNumber", invoiceNumber);
			hashMap.put("StartingDate", startingDateInclusive);
			hashMap.put("EndingDate", endingDateInclusive);
			
			return hashMap;
		}
	}
}
