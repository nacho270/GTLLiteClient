package ar.com.lite.textillevel.gui.util;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import ar.com.fwcommon.boss.BossIO;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.FWJTable;
import ar.com.fwcommon.util.DateUtil;
import ar.com.fwcommon.util.FileUtil;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.fwcommon.util.StringUtil;
import ar.com.fwcommon.util.SwingWorker;
import ar.com.textillevel.entidades.config.ParametrosGenerales;
import ar.com.textillevel.entidades.cuenta.to.ETipoDocumento;
import ar.com.textillevel.facade.api.remote.ParametrosGeneralesFacadeRemote;
import ar.com.textillevel.util.GTLBeanFactory;

public class GenericUtils {

	private static final String REG_EXP_NUMERO = "-?[0-9]+([,|\\.][0-9]+)?";
	private static final Pattern pattern = Pattern.compile(REG_EXP_NUMERO);
	private static final Random rand = new Random();
	private static final Integer MARGEN_COLOR = 55;
	private static final Integer LIM_INF_COLOR = 55;
	private static final Integer LIM_SUP_COLOR = 200;
	private static NumberFormat df;
	private static NumberFormat df2;
	private static NumberFormat df3;
	private static NumberFormat df4;
	private static DecimalFormat dfMovimientos;
	private static DecimalFormat dfListaPrecios;
	private static final Double TOLEARANCIA_A_CERO = 0.009;
	
	/* Corresponden al DateUtil.getDia(date) */
	public static final int DIA_DOMINGO = 1; 
	public static final int DIA_LUNES = 2;
	public static final int DIA_MARTES = 3;
	public static final int DIA_MIERCOLES = 4;
	public static final int DIA_JUEVES = 5;
	public static final int DIA_VIERNES = 6;
	public static final int DIA_SABADO = 7;
	
	private static ParametrosGeneralesFacadeRemote paramGenFacade;
	
	static{
		df = DecimalFormat.getNumberInstance(new Locale("es_AR"));
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		df.setMinimumIntegerDigits(1);
		df.setGroupingUsed(true);
		
		df2 = DecimalFormat.getNumberInstance(new Locale("es_AR"));
		df2.setMaximumFractionDigits(4);
		df2.setMinimumFractionDigits(4);
		df2.setMinimumIntegerDigits(1);
		df2.setGroupingUsed(false);
		
		df3 = DecimalFormat.getNumberInstance(new Locale("es_AR"));
		df3.setMaximumFractionDigits(3);
		df3.setMinimumFractionDigits(3);
		df3.setMinimumIntegerDigits(1);
		df3.setGroupingUsed(false);
		
		df4 = new DecimalFormat("#,###.00");
		df4.setMaximumFractionDigits(2);
		df4.setGroupingUsed(true);
		
		dfMovimientos = (DecimalFormat) DecimalFormat.getNumberInstance(new Locale("es_AR"));// new DecimalFormat("#,###.00");
		dfMovimientos.setMaximumFractionDigits(2);
		dfMovimientos.setMinimumFractionDigits(2);
		dfMovimientos.setGroupingUsed(true);
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.');
		dfMovimientos.setDecimalFormatSymbols(symbols);
		
		dfListaPrecios = (DecimalFormat) DecimalFormat.getNumberInstance(new Locale("es_AR"));
		dfListaPrecios.setMaximumFractionDigits(3);
		dfListaPrecios.setMinimumFractionDigits(3);
		dfListaPrecios.setMinimumIntegerDigits(1);
	}
	
	
	public static boolean esNumerico(String text) {
		if(text == null) {
			return false;
		}
		text = text.replaceAll(",", "").replaceAll("\\.", "");
		Matcher matcher = pattern.matcher(text.trim());
		return matcher.matches();
	}
	
	public static boolean isUnix(){
		String os = System.getProperty("os.name").toLowerCase();
	    return (os.indexOf("nix") >=0 || os.indexOf("nux") >=0);
	}

	public static boolean isWindows(){
		String os = System.getProperty("os.name").toLowerCase();
	    return (os.indexOf("win" ) >= 0); 
	}
	
	public static GridBagConstraints createGridBagConstraints(int x, int y, int posicion, int fill, Insets insets, int cantX, int cantY, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = posicion;
		gbc.fill = fill;
		gbc.insets = insets;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = cantX;
		gbc.gridheight = cantY;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		return gbc;
	}
	
	public static Color getRandomColor() {
		Color color = new Color(rand.nextInt(256), 	//RED
								rand.nextInt(256), 	//GREEN
								rand.nextInt(256));	//BLUE
		while(!checkColor(color)){
			color = new Color(rand.nextInt(256), 	//RED
							  rand.nextInt(256), 	//GREEN
							  rand.nextInt(256));	//BLUE
		}
        return color;	
    }
	
	private static boolean checkColor(Color color){
		return (checkRed(color) || checkGreen(color) || checkBlue(color)) && checkRange(color);
	}
	
	private static boolean checkRange(Color color) {
		return	(color.getRed()<LIM_SUP_COLOR && color.getRed()>LIM_INF_COLOR) &&
				(color.getBlue()<LIM_SUP_COLOR && color.getBlue()>LIM_INF_COLOR) &&
				(color.getGreen()<LIM_SUP_COLOR && color.getGreen()>LIM_INF_COLOR);
	}

	private static boolean checkRed(Color color) {
		return (255-color.getRed()>MARGEN_COLOR) && MARGEN_COLOR<=color.getGreen() && MARGEN_COLOR<=color.getBlue();
	}

	private static boolean checkGreen(Color color) {
		return (255-color.getGreen()>MARGEN_COLOR) && MARGEN_COLOR<=color.getRed() && MARGEN_COLOR<=color.getBlue();
	}
	
	private static boolean checkBlue(Color color) {
		return (255-color.getBlue()>MARGEN_COLOR) && MARGEN_COLOR<=color.getGreen() && MARGEN_COLOR<=color.getRed();
	}
	
	public static String convertirNumeroATexto(Double numero){
		if(numero<0){
			return "";
		}
		if(Math.floor(numero.doubleValue())-Math.abs(numero.doubleValue())==0){
			return Num2Text.getInstance().convertirLetras(numero.intValue());
		}else{
			String strNro = String.valueOf(numero);
			String pe = strNro.substring(0, strNro.indexOf('.'));
			String pd = strNro.substring(strNro.indexOf('.')+1,strNro.length());
			if(pd.length() >= 2) {
				pd = pd.substring(0,2);
			}
			pd = (pd.length()==1?pd+"0":pd);
			return Num2Text.getInstance().convertirLetras(Integer.parseInt(pe)) + " con " + Num2Text.getInstance().convertirLetras(Integer.parseInt(pd));
		}
	}
	
	public static <T> Collection<T> restaConjuntosOrdenada(Collection<T> izq, Collection<T> der) {
		ArrayList<T> aux = new ArrayList<T>();
		for (T obj : izq) {
			if (!der.contains(obj)) {
				aux.add(obj);
			}
		}
		return aux;
	}
	
	public static void exportarAExcel(FWJTable tabla, String titulo, String subtitulo, String filtros, String ruta, boolean intercalar) {
		ar.com.fwcommon.util.MiscUtil.exportarAExcel(tabla, titulo, subtitulo, filtros, ruta, null, intercalar);
	}
	
	public static void ponerFondoAnulada(final JScrollPane sp) {
		String url = "ar/com/textillevel/imagenes/fondo_anulada.png";
		sp.setBackground(Color.WHITE);  
		sp.getViewport().setOpaque(false);  
		final Border bkgrnd;
		try {
			InputStream resourceAsStream = FileUtil.getResourceAsStream(url);
			bkgrnd = new CentredBackgroundBorder(ImageIO.read(resourceAsStream));
			Runnable r = new Runnable() {  
		        public void run() { 
					sp.setViewportBorder(bkgrnd);  
		        	sp.repaint();  
		        }  
		    };  
		    SwingUtilities.invokeLater(r);  
		} catch (IOException e) {
			FWJOptionPane.showErrorMessage(null, e.getMessage(), "Error");
		}
	}
	
	public static Dimension getDimensionPantalla() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		return tk.getScreenSize();
	}

	public static NumberFormat getDecimalFormat() {
		return df;
	}

	public static NumberFormat getDecimalFormat2() {
		return df2;
	}
	
	public static NumberFormat getDecimalFormat3() {
		return df3;
	}
	
	public static NumberFormat getDecimalFormatFactura(){
		return df4;
	}
	
	public static NumberFormat getDecimalFormatListaPrecios(){
		return dfListaPrecios;
	}
	
	public static NumberFormat getDecimalFormatTablaMovimientos() {
		return dfMovimientos;
	}
	
	public static boolean esHoy(java.sql.Date fecha){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(DateUtil.getAhora());
		return cal.get(GregorianCalendar.DAY_OF_MONTH)==DateUtil.getDia(fecha) && 
			   cal.get(GregorianCalendar.MONTH)==DateUtil.getMes(fecha) && 
			   cal.get(GregorianCalendar.YEAR)== DateUtil.getAnio(fecha);
	}
	
	public static String fixPrecioCero(String format) {
		if(!StringUtil.isNullOrEmpty(format) && (format.equals(",00") || format.equals(".00"))) {
			return "0,00";
		}
		if(format.indexOf(",")==-1 && format.indexOf(".")==-1){
			return format+",00";
		}
		return format;
	}
	
	public static int restarFechas(Date fechaInicial, Date fechaFinal) {
		long fechaInicialMs = fechaInicial.getTime();
		long fechaFinalMs = fechaFinal.getTime();
		long diferencia = fechaFinalMs - fechaInicialMs;
		double dias = Math.floor(diferencia / (1000 * 60 * 60 * 24));
		return ((int) dias);
	}

	public static void main(String[] args) {
		String nn = "234";
		System.out.println(esNumerico(nn));
		System.out.println(StringUtil.ponerMayuscula(ETipoDocumento.FACTURA.toString().toLowerCase().replaceAll("_", " de ").replaceAll("debito", "d�bito").replaceAll("credito", "cr�dito")));
		System.out.println(StringUtil.ponerMayuscula(ETipoDocumento.NOTA_DEBITO.toString().toLowerCase().replaceAll("_", " de ").replaceAll("debito", "d�bito").replaceAll("credito", "cr�dito")));
		System.out.println(StringUtil.ponerMayuscula(ETipoDocumento.NOTA_CREDITO.toString().toLowerCase().replaceAll("_", " de ").replaceAll("debito", "d�bito").replaceAll("credito", "cr�dito")));
	}

	public static boolean esCero(Double numero){
		return Math.abs(numero) < TOLEARANCIA_A_CERO;
	}

	public static float getFloatValueInJTextField(JTextField txt) {
		String contenidoTexto = txt.getText().trim();
		return Float.valueOf(contenidoTexto.replaceAll(",", "\\."));
	}

	public static double getDoubleValueInJTextField(JTextField txt) {
		String contenidoTexto = txt.getText().trim();
		return getDoubleValue(contenidoTexto);
	}

	public static String doubleToStringArg(Double d) {
		if(d == null) {
			return null;
		}
		String valorStr = getDecimalFormat().format(d);
		valorStr = swapSeparadores(valorStr);
		return valorStr;
	}

	public static String formatDoubleStringArg(String d) {
		if(d == null) {
			return null;
		}
		return swapSeparadores(d);
	}

	private static String swapSeparadores(String valorStr) {
		if(valorStr == null) {
			return null;
		}
		valorStr = valorStr.replaceAll(",", "#");
		valorStr = valorStr.replaceAll("\\.", ",");
		valorStr = valorStr.replaceAll("#", "\\.");
		return valorStr;
	}

	public static double getDoubleValue(String contenidoTexto) {
		if(contenidoTexto.indexOf("\\.") != -1) {
			contenidoTexto = contenidoTexto.replaceAll(".", ",");
		} /*else {
			return Double.valueOf(contenidoTexto.replaceAll(",", "\\."));
		}*/
		return Double.valueOf(contenidoTexto.replaceAll("\\.", "").replaceAll(",", "."));
	}
	
	public static void setValorComboSinListeners(JComboBox combo, Object valor){
		List<ItemListener> itemListenerList = Arrays.asList(combo.getItemListeners()) ;
		for (ItemListener itemListener : itemListenerList) {
			combo.removeItemListener(itemListener) ;
		}
		combo.setSelectedItem(valor);
		for (ItemListener itemListener : itemListenerList) {
			combo.addItemListener(itemListener) ;
		}
	}

	public static Integer getMaximo(Integer... numeros){
		Integer max = 0;
		for(Integer n : numeros){
			if(n > max){
				max = n;
			}
		}
		return max;
	}
	
	public static boolean isSistemaTest(){
		return System.getProperties().getProperty("test")!= null && System.getProperties().getProperty("test").equals("1");
	}

	public static String colorToHexa(Color color){
		String hex = Integer.toHexString(color.getRGB() & 0xffffff);
		if (hex.length() < 6) {
		    hex = "0" + hex;
		}
		return "#" + hex;
	}

	public static String repeat(String str, int cantVeces) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < cantVeces; i++) {
			result = result.append(str);
		}
		return result.toString();
	}

	public static String formatAsSuperIndex(String texto, boolean underline){
		int coma = texto.indexOf(",");
		if(coma == -1){
			return texto;
		}
		String decimales = texto.substring(coma + 1, texto.length());
		if(underline){
			return texto.substring(0, coma+1) + "<sup><u>" + decimales + "</u></sup>";
		}else{
			return texto.substring(0, coma+1) + "<sup>" + decimales + "</sup>";
		}
	}
	
	public interface BackgroundTask{
		public void perform();
	}
	
	public static void realizarOperacionConDialogoDeEspera(String textoEspera, final BackgroundTask task){
		SwingWorker sw = new SwingWorker() {
			@Override
			public Object construct() {
				task.perform();
				WaitDialog.stopWait();
				return 0;
			}
		};
		sw.start();
		WaitDialog.startWait(textoEspera);
	}
	
	public static BufferedImage createBarCode(String barCode) throws IOException {
		Code128Bean bean = new Code128Bean();
		bean.setHeight(10d);
		bean.doQuietZone(false);

		OutputStream out = new java.io.FileOutputStream(new File(System.getProperty("java.io.tmpdir") + File.pathSeparator + "output.png"));
		BitmapCanvasProvider provider = new BitmapCanvasProvider(out, "image/x-png",330, BufferedImage.TYPE_BYTE_GRAY, true, 0);
		bean.generateBarcode(provider, barCode);

		provider.finish();
		return provider.getBufferedImage();
	}

	public static void enviarEmail(String asunto, String cuerpo, List<File> files, List<String> to, List<String> cc) throws AddressException, MessagingException {
		Properties mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");

		Session gmailSession = Session.getDefaultInstance(mailServerProperties, null);
		Message mailMessage = new MimeMessage(gmailSession);

		addRecipents(to, mailMessage, Message.RecipientType.TO);
		addRecipents(cc, mailMessage, Message.RecipientType.CC);

		mailMessage.setSubject(asunto);
		mailMessage.setSentDate(new java.util.Date());
		
		Multipart multipart = new MimeMultipart();
		
		MimeBodyPart mimeBodyPartCuerpo = new MimeBodyPart();
		mimeBodyPartCuerpo.setContent(cuerpo, "text/html");
		multipart.addBodyPart(mimeBodyPartCuerpo);

		if (files != null && !files.isEmpty()) {
			for(File file : files) {
				MimeBodyPart mimeBodyPartFile = new MimeBodyPart();
				DataSource source = new FileDataSource(file);
				mimeBodyPartFile.setDataHandler(new DataHandler(source));
				mimeBodyPartFile.setFileName(file.getName());
				multipart.addBodyPart(mimeBodyPartFile);
			}
		}

		File emailIcon = null;
		if (!isSistemaTest()) {
			emailIcon = new File(System.getProperty("java.io.tmpdir") + "tempEmailIcon.png");
			try {
				MimeBodyPart imagePart = new MimeBodyPart();
				imagePart.setHeader("Content-ID", "<AbcXyz123>");
				imagePart.setDisposition(MimeBodyPart.INLINE);
				BossIO.getInstance().writeFile(emailIcon, FileUtil.getResourceAsStream("ar/com/textillevel/imagenes/logo-gtl-email.png"));
				imagePart.attachFile(emailIcon);
				multipart.addBodyPart(imagePart);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mailMessage.setContent(multipart);
		mailMessage.saveChanges();

		Transport transport = gmailSession.getTransport("smtp");
		
		ParametrosGenerales paramGen = getParamGenFacade().getParametrosGenerales();
		String user = paramGen.getUsernameCuentaMail();
		String pass = paramGen.getPasswordCuentaMail();
		if(StringUtil.isNullOrEmpty(user) || StringUtil.isNullOrEmpty(pass)) {
			throw new RuntimeException("Falta configurar el usaurio y password de la cuenta de envio de Mail.");
		}
		transport.connect("smtp.gmail.com", user, pass);
		transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
		transport.close();
		if (emailIcon != null) {
			emailIcon.delete();
		}
	}

	private static void addRecipents(List<String> to, Message mailMessage, Message.RecipientType type) throws MessagingException, AddressException {
		for (String recipent : to) {
			try {
				InternetAddress internetAddress = new InternetAddress(recipent);
				internetAddress.validate();
				mailMessage.addRecipient(type, internetAddress);
			} catch (AddressException ae) {
				System.err.println("Email erroneo: " + recipent);
				throw ae;
			}
		}
	}
	
	public static class SiNoResponse {
		private int respose;
		private boolean noVolverAPreguntar;

		public SiNoResponse(int respose, boolean noVolverAPreguntar) {
			this.respose = respose;
			this.noVolverAPreguntar = noVolverAPreguntar;
		}

		public int getRespose() {
			return respose;
		}
		
		public boolean isNoVolverAPreguntar() {
			return noVolverAPreguntar;
		}
	}
	
	public static SiNoResponse realizarPregunta(Frame padre, String pregunta, String titulo) {
		JDialogSiNoNoVolverAPreguntar d = new JDialogSiNoNoVolverAPreguntar(padre, pregunta, titulo);
		d.setVisible(true);
		return new SiNoResponse(d.getResponse(), d.noVolverAPreguntar());
	}
	
	public static SiNoResponse realizarPregunta(Dialog padre, String pregunta, String titulo) {
		JDialogSiNoNoVolverAPreguntar d = new JDialogSiNoNoVolverAPreguntar(padre, pregunta, titulo);
		d.setVisible(true);
		return new SiNoResponse(d.getResponse(), d.noVolverAPreguntar());
	}

	public static boolean isEmailValido(String email) {
		try {
			new InternetAddress(email).validate();
			return true;
		}catch(Exception e){
			return false;
		}
	}

	private static ParametrosGeneralesFacadeRemote getParamGenFacade() {
		if(paramGenFacade == null) {
			paramGenFacade = GTLBeanFactory.getInstance().getBean2(ParametrosGeneralesFacadeRemote.class);
		}
		return paramGenFacade;
	}

	public static void showTemporaryDialog(final int waitTime, final String titulo, final JOptionPane pane) {
        final JDialog dialog = pane.createDialog(titulo);
        GuiUtil.setEstadoPanel(pane, false);
        final Timer timer = new Timer(waitTime, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();				
			}
		});
        timer.start();
        dialog.setVisible(true);
        dialog.dispose();
    }
}