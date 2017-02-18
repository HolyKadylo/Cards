class App implements Actions{
	public static void main(String[] args){
		Call call = new Call();
		App app = new App();
		System.out.println("HW");
		app.call();
		app.recall();
		app.isSuccessfull(call);
		System.out.println(iii);
	}
	public void call(){
		System.out.println("call");
	}
	public void recall(){
		System.out.println("recall");
	}
	public void isSuccessfull(Call call){
		System.out.println(call.call);
	}
}

interface Actions {
	String iii = "Howdy Partner";
	void call();
	void recall();
	void isSuccessfull(Call call);
}