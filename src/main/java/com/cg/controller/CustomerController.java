package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.service.customer.ICustomerService;
import com.cg.service.deposit.IDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IDepositService depositService;

    @GetMapping
    public String showListPage(Model model) {
        List<Customer> customers = customerService.findAll();

        model.addAttribute("customers", customers);

        return "customer/list";
    }

    @GetMapping("/create")
    public String showCreatePage() {
        return "customer/create";
    }

    @GetMapping("/deposit/{customerId}")
    public String showDepositPage(@PathVariable Long customerId, Model model) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "ID khách hàng không tồn tại");
        }
        else {
            Customer customer = customerOptional.get();
            Deposit deposit = new Deposit();
            deposit.setCustomer(customer);

            model.addAttribute("deposit", deposit);
        }

        return "customer/deposit";
    }


    @PostMapping("/create")
    public String doCreate(@ModelAttribute Customer customer) {

        customer.setId(null);
        customer.setBalance(BigDecimal.ZERO);
        customerService.save(customer);

        return "customer/create";
    }

    @PostMapping("/deposit/{customerId}")
    public String doDeposit(@ModelAttribute Deposit deposit, @PathVariable Long customerId, Model model) {
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "ID khách hàng không tồn tại");
        }
        else {
            Customer customer = customerOptional.get();

            deposit.setId(null);
            depositService.save(deposit);

            BigDecimal currentBalance = customer.getBalance();
            BigDecimal newBalance = currentBalance.add(deposit.getTransactionAmount());
            customer.setBalance(newBalance);
            customerService.save(customer);

            deposit.setCustomer(customer);

            model.addAttribute("deposit", deposit);
        }

        return "customer/deposit";
    }
}
