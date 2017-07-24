import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		// Setting up the bot
		AIMLParser ai = new AIMLParser();
		
		// First we must create one
		ai.CreateBot();
		
		// Here the bot's tree file so that it will know it aiml files
		ai.setTree("brain/files/tree.xml");
		
		
		
		// Setting bot's info. there are many you can check in the class AIML.java
		
		ai.setInfo("name", "Alice","bot");
		ai.setInfo("master", "Karrar S. Honi","bot");
		ai.setInfo("birthday", "2017/7/29","bot");
		
		
		Scanner io = new Scanner(System.in);
		System.out.println("now am ready!:");
		System.out.print("Me:");
		String input = io.nextLine();
		System.out.println("");
		while(!input.equals("end")){
			//getting a reply from the AIMLParser for a given input
			String reply = ai.reply(input);
			System.out.println("bot:"+reply);
			System.out.print("Me:");
			input = io.nextLine();
		}
		
		
		

	}

}
