package recipes;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.entity.Recipe;
import recipes.exception.DbException;
import recipes.service.RecipeService;

public class Recipes {
	private Scanner scanner = new Scanner(System.in);
	private RecipeService recipeService = new RecipeService();
	private Recipe curRecipe;
	
	// @formatter:off
	private List<String> operations = List.of(
	
		"1) Create and populate all tables",
		"2) Add a recipe",
		"3) List recipes",
		"4) Select working recipe"
	);
	// @formatter:on
	public static void main(String[] args) {
		
		new Recipes().displayMenu();
	}
	
	private void displayMenu() {
		boolean done = false;

		while (!done) {
			
			try {
			  int operation = getOperation();
				switch (operation) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createTables();
					break;

				case 2:
					addRecipe();
					break;
				
				case 3:
					listRecipes();
					break;
				
				case 4:
					setCurrentRecipe();
					break;
					
				default:
					System.out.println("\n" + operation + " is not valid. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + "Try again.");
			}
		}
	}
			
	private void setCurrentRecipe() {
		List<Recipe> recipes = listRecipes();
		
		Integer recipeId = getIntInput ("Select a recipe ID");
		
		curRecipe = null;
		
		for (Recipe recipe : recipes) {
			if (recipe.getRecipeId().equals(recipeId)) {
				curRecipe = recipeService.fetchRecipeById(recipeId);
				break;
			}
		}
		
		if (Objects.isNull(curRecipe)) {
			System.out.println("\nInvalid recipe selected.");
		}
	}

	private void createTables() {
		recipeService.createAndPopulateTables();
		System.out.println("\nTables created and populated!");
	}
	
	private void addRecipe() {
		String name = getStringInput("Enter the recipe name");
		String notes = getStringInput("Enter recipe notes");
		Integer numServings = getIntInput("Enter the number of servings");
		Integer prepMinutes = getIntInput("Enter prep time in minutes");
		Integer cookMinutes = getIntInput("Enter cook time in minutes");
		
		LocalTime prepTime = minutesToLocalTime(prepMinutes);
		LocalTime cookTime = minutesToLocalTime(cookMinutes);
		
		Recipe recipe = new Recipe();
		
		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(numServings);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);
		
		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipe:\n" + dbRecipe);
		
		curRecipe = recipeService.fetchRecipeById(dbRecipe.getRecipeId());
	}

	private List<Recipe> listRecipes() {
		List<Recipe> recipes = recipeService.fetchAllRecipes();
		
		System.out.println("\nRecipes:");
		
//		// I decided to use the for loop suggested by Dr. Martin from the video
//		// for loop may be causing errors. Commenting out and following Dr. Martin's original lambda expression
////		for (Recipe recipe : recipes) {
////			System.out.println("    " + recipe.getRecipeId() + ": " + recipe.getRecipeName());
////		}
		recipes.forEach(recipe -> System.out.println("    " + recipe.getRecipeId() + ": " + recipe.getRecipeName()));
		
		return recipes;
	}
	
	private LocalTime minutesToLocalTime(Integer numMinutes) {
		int min = Objects.isNull(numMinutes) ? 0 : numMinutes;
		int hours = min / 60;
		int minutes = min % 60;
		
		return LocalTime.of(hours, minutes);
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu. TTFN!");
		return true;
	}

	private int getOperation() {
		printOperations();
		Integer op = getIntInput("\nEnter an operation number (press Enter to quit)");
		
		return Objects.isNull(op) ? -1 : op;
	}

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do:");
		
		operations.forEach(op -> System.out.println("   " + op));
		
		if (Objects.isNull(curRecipe)) {
			System.out.println("\nYou are not working with a recipe.");
		} else {
			System.out.println("\nYou are working with recipe " + curRecipe);
		}
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.parseInt(input);
		}
		catch (NumberFormatException e) {
				throw new DbException(input + " is not a valid number."); 
			}
		}
	
	@SuppressWarnings("unused") 
	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number."); 
			}
	}
	
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();
		return line.isBlank() ? null : line.trim();
	}
}