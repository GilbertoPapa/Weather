package br.com.gilbertopapa.android.webservice;

// Exceção ocorrida na invocação de um web service
public class WebServiceException extends Exception {

	public WebServiceException() {
	}

	public WebServiceException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public WebServiceException(String detailMessage) {
		super(detailMessage);
	}

	public WebServiceException(Throwable throwable) {
		super(throwable);
	}
}
