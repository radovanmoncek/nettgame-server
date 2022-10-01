import java.util.Scanner;

public final class MemeImplementation{

    public static void main(String[] args){
        
        System.out.println("Odpovezte ano nebo ne");

        if(askQuestion("Funguje ten kram?").equals("ano")) System.out.println("Nestourejte se v nem"); 
         
        else{

            if(askQuestion("Stoural jste se v nem?").equals("ano")){

                System.out.println("Jste nemehlo");

                if(askQuestion("Videl vas nekdo?").equals("ano")){

                    System.out.println("JSTE BLB!");

                    if(askQuestion("Mate to na koho svest?").equals("ne")) System.out.println("JSTE BLB!");
                }
                else{

                    System.out.println("Ututlejte to");

                    if(askQuestion("Povedlo se?").equals("ne")){

                        System.out.println("JSTE BLB!");

                        if(askQuestion("Mate to na koho svest?").equals("ne")) System.out.println("JSTE BLB!");
                    } 
                }
            }
            else{

                if(askQuestion("Schytate to?").equals("ano")){

                    System.out.println("JSTE BLB!");

                    if(askQuestion("Mate to na koho svest?").equals("ne")) System.out.println("JSTE BLB!");
                }
                else System.out.println("Nechte to plavat");
            }
        }  
        scanner.close();

        System.out.println("Neni co resit");
    }

    private static String askQuestion(String question){

        System.out.println(question);

        String userInput = scanner.nextLine();

        if(!userInput.equals("ano") && !userInput.equals("ne")) askQuestion(question);
        
        return userInput;
    }

    private static Scanner scanner = new Scanner(System.in);
}