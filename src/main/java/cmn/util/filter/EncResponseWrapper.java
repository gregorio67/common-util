
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class EncResponseWrapper extends HttpServletResponseWrapper {

	private ByteArrayOutputStream baos = null;

	private FilterServletOutputStream filterOutput = null;
	
	public EncResponseWrapper(HttpServletResponse response) {
		super(response);
		baos = new ByteArrayOutputStream();
	}
	
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (filterOutput == null) {
			filterOutput = new FilterServletOutputStream(baos);
		}
		return filterOutput;
	}
	
	public byte[] getDataStream() {
		return baos.toByteArray();
	}
	
	public class FilterServletOutputStream extends ServletOutputStream {

		private DataOutputStream output = null;
		
		FilterServletOutputStream(OutputStream output) {
			this.output = new DataOutputStream(output);
		}
		@Override
		public void write(int b) throws IOException {
			output.write(b);
		}
		
		@Override
		public void write(byte[] b, int start, int end) throws IOException {
			output.write(b, start, end);
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			output.write(b);
		}
		@Override
		public boolean isReady() {
			return false;
		}
		@Override
		public void setWriteListener(WriteListener arg0) {
			
		}
	}

}
