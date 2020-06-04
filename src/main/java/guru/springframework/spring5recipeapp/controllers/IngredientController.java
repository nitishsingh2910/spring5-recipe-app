package guru.springframework.spring5recipeapp.controllers;

import guru.springframework.spring5recipeapp.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class IngredientController {

    RecipeService recipeService;

    public IngredientController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @RequestMapping("recipe/{id}/ingredients")
    public String viewIngredients(@PathVariable String id, Model model){
        log.debug("Getting ingredient list for recipe id "+id);
        model.addAttribute(recipeService.findById(Long.valueOf(id)));
        return "recipe/ingredient/list";
    }
}
