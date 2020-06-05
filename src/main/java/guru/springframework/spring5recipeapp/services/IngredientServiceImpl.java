package guru.springframework.spring5recipeapp.services;

import guru.springframework.spring5recipeapp.commands.IngredientCommand;
import guru.springframework.spring5recipeapp.converters.IngredientCommandToIngredient;
import guru.springframework.spring5recipeapp.converters.IngredientToIngredientCommand;
import guru.springframework.spring5recipeapp.domain.Ingredient;
import guru.springframework.spring5recipeapp.domain.Recipe;
import guru.springframework.spring5recipeapp.repositories.RecipeRepository;
import guru.springframework.spring5recipeapp.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final RecipeRepository recipeRepository;
    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;

    public IngredientServiceImpl(RecipeRepository recipeRepository,
                                 IngredientToIngredientCommand ingredientToIngredientCommand,
                                 UnitOfMeasureRepository unitOfMeasureRepository,
                                 IngredientCommandToIngredient ingredientCommandToIngredient) {
        this.recipeRepository = recipeRepository;
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
    }

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if(! recipeOptional.isPresent())
            log.error("recipe id not found. Id: "+recipeId);
        Recipe recipe = recipeOptional.get();

        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(ingredient -> ingredientToIngredientCommand.convert(ingredient))
                .findFirst();

        if(! ingredientCommandOptional.isPresent())
            log.error("Ingredient id not found. Id: "+ ingredientId);

        return ingredientCommandOptional.get();
    }

    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(command.getRecipeId());
        if(! recipeOptional.isPresent()){
            log.error("Recipe not found for id: " + command.getRecipeId());
            return new IngredientCommand();
        }
        else{
            Recipe recipe = recipeOptional.get();
            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();
            if(ingredientOptional.isPresent()){
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(command.getDescription());
                ingredientFound.setAmount(command.getAmount());
                ingredientFound.setUom(unitOfMeasureRepository.findById(command.getUom().getId())
                        .orElseThrow(() -> new RuntimeException("UOM NOT FOUND")));     //todo address this
            }
            else{
                //add new ingredient
                recipe.addIngredient(ingredientCommandToIngredient.convert(command));
            }
            Recipe savedRecipe = recipeRepository.save(recipe);
            //to do check for fail
            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();

            //check by description
            if(! savedIngredientOptional.isPresent()){
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(ingredient -> ingredient.getDescription().equals(command.getDescription()))
                        .filter((ingredient -> ingredient.getAmount().equals(command.getAmount())))
                        .filter(ingredient -> ingredient.getUom().getId().equals(command.getUom().getId()))
                        .findFirst();
            }
            return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
        }
    }
}
