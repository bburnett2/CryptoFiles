package View;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class MainViewControl {
	@FXML
	private TextArea lengthGuessTA;
	@FXML
	private TextArea keywordTA;
	@FXML
	private TextArea plainTextTA;
	@FXML
	private TextArea cipherTextTA;
	@FXML
	private TextArea seriesNumTA;
	@FXML
	private TextArea probabilityWordLengthTA;
	@FXML
	private TextArea probabilityWordChoiceTA;
	@FXML
	private Button graphBt;
	@FXML
	private Button encryptBt;
	@FXML
	private Button  decryptBt;
	@FXML
	private BarChart<Character, Number> frequencyBC;

	private final String[] LETTERS = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", 
			"u", "v", "w", "x", "y", "z"};
	//Frequencies were pulled from wikipedia
	private final double[] FREQUENCIES = {8.167, 1.492, 2.782, 4.252, 12.702, 2.228, 2.015, 6.094, 9.966, 0.153, 0.772, 
			4.025, 2.406, 6.749, 7.507, 1.929, 0.095, 5.987, 6.327, 9.056, 2.758, 0.978, 2.360, 0.150, 1.974, 0.074};
	@FXML
	protected void initialize(){
		//System.out.println("worked");
	}

	@FXML
	public void analyis(){
		//read in cipher text and length guess
		String cipherText = cipherTextTA.getText().toLowerCase();
		String lg = lengthGuessTA.getText();
		int lengthGuess = 0;
		try{
			lengthGuess = Integer.parseInt(lg);
		}
		catch(Exception e){
			//change to pop-up
			System.out.println("enter a number");
		}
		String[] ciphers = new String[lengthGuess];
		for(int i = 0; i < ciphers.length; i++){
			ciphers[i] = "";
		}
		for(int i = 0; i < cipherText.length(); i++){
			ciphers[i%lengthGuess] += cipherText.charAt(i);
		}
		//System.out.println(ciphers[0]);
		//frequency on each shift
		ArrayList<Integer []> counts = new ArrayList<>();
		for(int i = 0; i < ciphers.length; i++){
			counts.add(countLetters(ciphers[i]));
		}
		//show graph
		showGraph(counts);
	}
	

	@FXML
	public void decrypt(){
		//read in cipher text and keyword
		String cipherText = cipherTextTA.getText().toLowerCase();
		String keyWord = keywordTA.getText();
		int keyLength = keyWord.length();
		//decrypt each shift
		StringBuilder plainText = new StringBuilder();
		for(int i = 0; i < cipherText.length(); i++){
			int current = getNumeric(cipherText.charAt(i));
			System.out.println("Letter: " + current + " " + cipherText.charAt(i));
			int key = getNumeric(keyWord.charAt(i%keyLength));
			System.out.println("Key: " + key + " " + keyWord.charAt(i%keyLength));
			plainText.append(getLetter(Math.floorMod(current-key, 26)));
			System.out.println(Math.floorMod(current-key, 26));
		}
		//output to plaintext
		plainTextTA.setText(plainText.toString());
		//show index of probability for this shift
		calculateProbabilityWordChoice(plainText.toString(), keyWord);
	}

	@FXML 
	public void encrypt(){
		//read in plaintext and keyword
		String plainText = plainTextTA.getText().toLowerCase();
		String keyWord = keywordTA.getText();
		int keyLength = keyWord.length();
		//encrypt each shift
		StringBuilder cipherText = new StringBuilder();
		for(int i = 0; i < plainText.length(); i++){
			int current = getNumeric(plainText.charAt(i));
			int key = getNumeric(keyWord.charAt(i%keyLength));
			cipherText.append(getLetter((current + key)%26));
		}
		//output cipher text
		cipherTextTA.setText(cipherText.toString());
	}

	//takes in a set of counts an displays their frequency in a bar graph
	private void showGraph(ArrayList<Integer []> input){
		int seriesInt = Integer.parseInt(seriesNumTA.getText());
		
		frequencyBC.getData().removeAll(frequencyBC.getData());
		XYChart.Series trueSeries = new XYChart.Series();
		trueSeries.setName("True Frequencies");
		for(int i = 0; i < FREQUENCIES.length; i++){
			trueSeries.getData().add(new XYChart.Data(LETTERS[i], FREQUENCIES[i]));
		}
		frequencyBC.getData().add(trueSeries);
		
//		for(int i = 0; i < input.size(); i++){
//			Integer[] counts = input.get(i);
//			XYChart.Series series1 = new XYChart.Series();
//			series1.setName("frequency " + (i + 1));       
//			for(int k = 0; k < LETTERS.length; k++){
//				System.out.println(counts[k]+ " " + counts[26]);
//				series1.getData().add(new XYChart.Data(LETTERS[k],  1.0*counts[k]/counts[26]*100));
//			}
//			frequencyBC.getData().add(series1);
//		}
		Integer[] counts = input.get(seriesInt - 1);
//		for(int i = 0; i < counts.length; i++){
//			System.out.println(counts[i]);
//		}
		XYChart.Series testSeries = new XYChart.Series();
		testSeries.setName("frequency " + (seriesInt));       
		for(int k = 0; k < LETTERS.length; k++){
			//System.out.println(counts[k]+ " " + counts[26]);
			testSeries.getData().add(new XYChart.Data(LETTERS[k],  1.0*counts[k]/counts[26]*100));
		}
		frequencyBC.getData().add(testSeries);
		frequencyBC.setBarGap(0);
		Axis<Character> xAxis = frequencyBC.getXAxis();
		NumberAxis yAxis = (NumberAxis) frequencyBC.getYAxis();
		calculateProbabilityWordLength(counts);
	}
	
	private void calculateProbabilityWordLength(Integer[] counts){
		double z = 0;
		for (int i = 0; i < FREQUENCIES.length; i++){
			z+= Math.pow(1.0*counts[i]/counts[26], 2);
			//System.out.println(z);
		}
		String prob = String.format("%.4f", z);
		//System.out.println(prob);
		probabilityWordLengthTA.setText(prob);
	}
	
	private void calculateProbabilityWordChoice(String text, String keyword){
		System.out.println("prob word choice");
		int length = keyword.length();
		String[] ciphers = new String[length];
		for(int i = 0; i < ciphers.length; i++){
			ciphers[i] = "";
		}
		for(int i = 0; i < text.length(); i++){
			ciphers[i%length] += text.charAt(i);
		}
		//System.out.println(ciphers[0]);
		//frequency on each shift
		ArrayList<Integer []> counts = new ArrayList<>();
		for(int i = 0; i < ciphers.length; i++){
			counts.add(countLetters(ciphers[i]));
		}
		int series = 1;
		try{
			series = Integer.parseInt(seriesNumTA.getText());
		}
		catch(Exception e){
			System.out.println("enter valid number");
		}
		Integer[] count = counts.get(series - 1);
		double z = 0;
		for (int i = 0; i < FREQUENCIES.length; i++){
			z+= FREQUENCIES[i]/100 * (1.0*count[i]) /count[26];
		}
		String prob = String.format("%.4f", z);
		probabilityWordChoiceTA.setText(prob);
		System.out.println(prob);
	}

	//returns an int with counts of the letters a-z in position 0-25
	//and a total count with no spaces in position 26
	private Integer[] countLetters(String str){
		int[] count = new int[27];
		for(int i = 0; i < str.length(); i++){
			char letter = str.charAt(i);
			switch (letter) {
			case 'a': count[0] += 1; break;
			case 'b': count[1]+= 1;break;
			case 'c': count[ 2]+= 1;break;
			case 'd': count[ 3]+= 1;break;
			case 'e': count[ 4]+= 1;break;
			case 'f': count[ 5]+= 1;break;
			case 'g': count[ 6]+= 1;break;
			case 'h': count[ 7]+= 1; break;
			case 'i': count[ 8]+= 1;break;
			case 'j': count[ 9]+= 1;break;
			case 'k': count[ 10]+= 1;break;
			case 'l': count[ 11]+= 1;break;
			case 'm': count[ 12]+= 1;break;
			case 'n': count[ 13]+= 1;break;
			case 'o': count[ 14]+= 1; break;
			case 'p': count[ 15]+= 1;break;
			case 'q': count[ 16]+= 1;break;
			case 'r': count[ 17]+= 1;break;
			case 's': count[ 18]+= 1;break;
			case 't': count[ 19]+= 1;break;
			case 'u': count[ 20]+= 1;break;
			case 'v': count[ 21]+= 1;break;
			case 'w': count[ 22]+= 1;break;
			case 'x': count[ 23]+= 1;break;
			case 'y': count[ 24]+= 1;break;
			case 'z': count[ 25]+= 1;break;
			}
		}
		count[26] = (str.replace(" ", "")).length();
		Integer[] ret = new Integer[27];
		for(int i = 0; i < count.length; i ++){
			ret[i]= (Integer)count[i];
		}
		return ret;
	}
	private char getLetter(int num){
		char ret;
		switch (num) {
		case 0: ret = 'a'; break;
		case 1: ret = 'b';break;
		case 2: ret = 'c';break;
		case 3: ret = 'd';break;
		case 4: ret = 'e';break;
		case 5: ret = 'f';break;
		case 6: ret = 'g';break;
		case 7: ret = 'h'; break;
		case 8: ret = 'i';break;
		case 9: ret = 'j';break;
		case 10: ret = 'k';break;
		case 11: ret = 'l';break;
		case 12: ret = 'm';break;
		case 13: ret = 'n';break;
		case 14: ret = 'o'; break;
		case 15: ret = 'p';break;
		case 16: ret = 'q';break;
		case 17: ret = 'r';break;
		case 18: ret = 's';break;
		case 19: ret = 't';break;
		case 20: ret = 'u';break;
		case 21: ret = 'v';break;
		case 22: ret = 'w';break;
		case 23: ret = 'x';break;
		case 24: ret = 'y';break;
		case 25: ret = 'z';break;
		default: ret = ' ';
		}
		return ret;
	}
	private int getNumeric(char letter){
		int ret = 0;
		switch (letter) {
		case 'a': ret = 0; break;
		case 'b': ret = 1;break;
		case 'c': ret = 2;break;
		case 'd': ret = 3;break;
		case 'e': ret = 4;break;
		case 'f': ret = 5;break;
		case 'g': ret = 6;break;
		case 'h': ret = 7; break;
		case 'i': ret = 8;break;
		case 'j': ret = 9;break;
		case 'k': ret = 10;break;
		case 'l': ret = 11;break;
		case 'm': ret = 12;break;
		case 'n': ret = 13;break;
		case 'o': ret = 14; break;
		case 'p': ret = 15;break;
		case 'q': ret = 16;break;
		case 'r': ret = 17;break;
		case 's': ret = 18;break;
		case 't': ret = 19;break;
		case 'u': ret = 20;break;
		case 'v': ret = 21;break;
		case 'w': ret = 22;break;
		case 'x': ret = 23;break;
		case 'y': ret = 24;break;
		case 'z': ret = 25;break;
		default: ret = -1;
		}
		return ret;
	}
}
