package hu.webuni.transport.web;

public class MyError {

	private String errorCode; //Nem int-re tettem, mert String-ként lehet beszédesebb magyarázat
	private String errorMessage;
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public MyError(String errorCode, String errorMessage) {		
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public MyError() {
		
	}
	
}
