package com.naumen.anticafe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.repository.ProductRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public String getAllProducts(Model model) {
        // Извлечение всех продуктов из базы данных
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "product";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        // Отобразить форму для добавления нового продукта
        model.addAttribute("product", new Product());
        return "product_add";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product) {
        // Сохранить новый продукт в базе данных
        productRepository.save(product);
        return "redirect:/product";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        // Найти продукт по идентификатору и отобразить форму для редактирования
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            model.addAttribute("product", optionalProduct.get());
            return "product_add";
        } else {
            return "redirect:/product";
        }
    }

    @PostMapping("/edit")
    public String editProduct(@ModelAttribute Product product) {
        // Обновить информацию о продукте в базе данных
        productRepository.save(product);
        return "redirect:/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        // Удалить продукт из базы данных
        productRepository.deleteById(id);
        return "redirect:/product";
    }
}
