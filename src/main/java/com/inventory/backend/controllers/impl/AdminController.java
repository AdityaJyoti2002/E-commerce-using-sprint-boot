package com.inventory.backend.controllers.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.inventory.backend.entities.Category;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.entities.CustomerOrder;
import com.inventory.backend.entities.Employee;
import com.inventory.backend.entities.Manufacturer;
import com.inventory.backend.entities.ManufacturerOrder;
import com.inventory.backend.entities.OrderReturns;
import com.inventory.backend.entities.Product;
import com.inventory.backend.entities.ShippingInfoCustomerOrder;
import com.inventory.backend.entities.ShippingInfoManufacturerOrder;
import com.inventory.backend.services.impl.CategoryServiceImpl;
import com.inventory.backend.services.impl.CustomerOrderService;
import com.inventory.backend.services.impl.CustomerService;
import com.inventory.backend.services.impl.EmployeeService;
import com.inventory.backend.services.impl.ManufacturerOrderService;
import com.inventory.backend.services.impl.ManufacturerService;
import com.inventory.backend.services.impl.OrderReturnsService;
import com.inventory.backend.services.impl.ProductService;
import com.inventory.backend.services.impl.SalesReportService;
import com.inventory.backend.services.impl.ShippingInfoCustomerOrderService;
import com.inventory.backend.services.impl.ShippingInfoManufacturerOrderService;

@Controller
public class AdminController {
    
    private ManufacturerService manufacturerService;

    private ProductService productService;

    private EmployeeService employeeService;

    private CategoryServiceImpl categoryService;

    private CustomerOrderService customerOrderService;

    private SalesReportService salesReportService;

    private CustomerService customerService;

    private ManufacturerOrderService manufacturerOrderService;

    private ShippingInfoCustomerOrderService shippingInfoCustomerOrderService;

    private ShippingInfoManufacturerOrderService shippingInfoManufacturerOrderService;

    private OrderReturnsService orderReturnsService;

    public AdminController(ManufacturerService manufacturerService,
                                 ProductService productService,
                                  EmployeeService employeeService,
                                   CategoryServiceImpl categoryService,
                                    CustomerOrderService customerOrderService,
                                     SalesReportService salesReportService,
                                      CustomerService customerService,
                                       ManufacturerOrderService manufacturerOrderService,
                                        ShippingInfoCustomerOrderService shippingInfoCustomerOrderService,
                                         ShippingInfoManufacturerOrderService shippingInfoManufacturerOrderService,
                                          OrderReturnsService orderReturnsService) {
        this.manufacturerService = manufacturerService;
        this.productService = productService;
        this.employeeService = employeeService;
        this.categoryService = categoryService;
        this.customerOrderService = customerOrderService;
        this.salesReportService = salesReportService;
        this.customerService = customerService;
        this.manufacturerOrderService = manufacturerOrderService;
        this.shippingInfoCustomerOrderService = shippingInfoCustomerOrderService;
        this.shippingInfoManufacturerOrderService = shippingInfoManufacturerOrderService;
        this.orderReturnsService = orderReturnsService;
    }

    @GetMapping("/admin/categories")
    public String showCategories(Model model) {

        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories";
    }

    @GetMapping("admin/dashboard")
    public String showDashboard(Model model) {
        Map<String, Double> totalSalesByCategory = customerOrderService.totalSalesByCategory();
        model.addAttribute("totalSalesByCategory", totalSalesByCategory);
        TreeMap<String, Integer> stockQuantityByCategory = new TreeMap<>(productService.stockQuantityByCategory());
        model.addAttribute("stockQuantityByCategory", stockQuantityByCategory);
        return "admin/dashboard";
    }

    @GetMapping("admin/categories/create")
    public String createCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/create/categoryCreateForm";
    }

    @RequestMapping(path = "admin/categories/create", method = RequestMethod.POST)
    public String createCategory(@ModelAttribute("category") Category category) {
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("admin/categories/update")
    public String updateCategory(@RequestParam Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id).get());
        return "admin/update/categoryUpdateForm";
    }

    @PostMapping("admin/categories/update")
    public String updateCategory(@ModelAttribute("category") Category category, @RequestParam Long id) {
        categoryService.update(category, id);
        return "redirect:/admin/categories";
    }

    @GetMapping("admin/categories/delete")
    public String deleteCategory(@RequestParam Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
    
    @GetMapping("admin/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "admin/products";
    }

    @GetMapping("admin/products/create")
    public String createProduct(Model model) {
        model.addAttribute("product", new Product());
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        List<Manufacturer> manufacturers = manufacturerService.findAll();
        model.addAttribute("manufacturers", manufacturers);
        return "admin/create/productCreateForm";
    }

    private String saveImage(MultipartFile image, Long id) throws IOException {

        String uploadDir = "uploads/";

        if (image.isEmpty()) {
            return null;
        }

        String filename = image.getOriginalFilename();
        Path path = Paths.get(uploadDir, filename + "_" + id);

        Files.createDirectories(Paths.get(uploadDir));
        Files.write(path, image.getBytes());


        return path.toString();
    }

    @PostMapping("admin/products/create")
    public String createProduct(@ModelAttribute("product") Product product,
                                @RequestParam("manufacturerIds") List<Long> manufacturerIds,
                                @RequestParam("costPrices") List<Double> costPrices,
                                @RequestParam("categoryId") Long categoryId,
                                @RequestParam("image") MultipartFile image) throws IOException {
        product.setCategory(categoryService.findById(categoryId).get());
        TreeMap<Long, Double> manufacturerCostPriceMap = new TreeMap<>();
        for (int i = 0; i < manufacturerIds.size(); i++) {
            manufacturerCostPriceMap.put(manufacturerIds.get(i), costPrices.get(i));
        }
        product.setImageUrl(saveImage(image, product.getProductId()));
        productService.save(product, manufacturerCostPriceMap);
        return "redirect:/admin/products";
    }

    @GetMapping("admin/products/update")
    public String updateProduct(@RequestParam Long id, Model model) {
        Product product = productService.findById(id).get();
        model.addAttribute("product", product);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        List<Manufacturer> manufacturers = manufacturerService.findAll();
        model.addAttribute("manufacturers", manufacturers);

        List<Long> manufacturerIds = new LinkedList<>();
        List<Double> costPrices = new LinkedList<>();
        
        for (Product.Pair<Manufacturer, Double> manufacturer : product.getManufacturers()) {
            manufacturerIds.add(manufacturer.getFirst().getManufacturerId());
            costPrices.add(manufacturer.getSecond());
        }
        model.addAttribute("manufacturerIds", manufacturerIds);
        model.addAttribute("costPrices", costPrices);

        return "admin/update/productUpdateForm";
    }

    @PostMapping("admin/products/update")
    public String updateProduct(@ModelAttribute("product") Product product,
                                @RequestParam("manufacturerIds") List<Long> manufacturerIds,
                                @RequestParam("costPrices") List<Double> costPrices,
                                @RequestParam("categoryId") Long categoryId,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam Long id) throws IOException {
        product.setCategory(categoryService.findById(categoryId).get());
        Set<Product.Pair<Manufacturer, Double>> manufacturers = new HashSet<>();
        for (int i = 0; i < manufacturerIds.size(); i++) {
            Manufacturer manufacturer = manufacturerService.findById(manufacturerIds.get(i)).get();
            manufacturers.add(new Product.Pair<>(manufacturer, costPrices.get(i)));
        }
        System.out.println("Image: " + image);
        product.setManufacturers(manufacturers);
        product.setImageUrl(saveImage(image, product.getProductId()));
        productService.update(product, id);
        return "redirect:/admin/products";
    }

    @GetMapping("admin/products/delete")
    public String deleteProduct(@RequestParam Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
   
    @GetMapping("admin/manufacturers")
    public String showManufacturers(Model model) {
        model.addAttribute("manufacturers", manufacturerService.findAll());
        return "admin/manufacturers";
    }

    @GetMapping("admin/manufacturers/create")
    public String createManufacturer(Model model) {
        model.addAttribute("manufacturer", new Manufacturer());
        return "admin/create/manufacturerCreateForm";
    }

    @PostMapping("admin/manufacturers/create")
    public String createManufacturer(@ModelAttribute("manufacturer") Manufacturer manufacturer) {
        manufacturerService.save(manufacturer);
        return "redirect:/admin/manufacturers";
    }

    @GetMapping("admin/manufacturers/update")
    public String updateManufacturer(@RequestParam Long id, Model model) {
        model.addAttribute("manufacturer", manufacturerService.findById(id).get());
        return "admin/update/manufacturerUpdateForm";
    }

    @PostMapping("admin/manufacturers/update")
    public String updateManufacturer(@ModelAttribute("manufacturer") Manufacturer manufacturer, @RequestParam Long id) {
        manufacturerService.update(manufacturer, id);
        return "redirect:/admin/manufacturers";
    }

    @GetMapping("admin/manufacturers/delete")
    public String deleteManufacturer(@RequestParam Long id) {
        manufacturerService.delete(id);
        return "redirect:/admin/manufacturers";
    }

    @GetMapping("admin/employees")
    public String showEmployees(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "admin/employees";
    }

    @GetMapping("admin/employees/create")
    public String createEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        return "admin/create/employeeCreateForm";
    }

    @PostMapping("admin/employees/create")
    public String createEmployee(@ModelAttribute("employee") Employee employee) {
        employeeService.save(employee);
        return "redirect:/admin/employees";
    }

    @GetMapping("admin/employees/update")
    public String updateEmployee(@RequestParam Long id, Model model) {
        model.addAttribute("employee", employeeService.findById(id).get());
        return "admin/update/employeeUpdateForm";
    }

    @PostMapping("admin/employees/update")
    public String updateEmployee(@ModelAttribute("employee") Employee employee, @RequestParam Long id) {
        employeeService.update(employee, id);
        return "redirect:/admin/employees";
    }

    @GetMapping("admin/employees/delete")
    public String deleteEmployee(@RequestParam Long id) {
        employeeService.delete(id);
        return "redirect:/admin/employees";
    }


    @GetMapping("admin/customers")
    public String showCustomers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "admin/customers";
    }

    @GetMapping("admin/customers/create")
    public String createCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/create/customerCreateForm";
    }

    @PostMapping("admin/customers/create")
    public String createCustomer(@ModelAttribute("customer") Customer customer, Model model) {
        try {
            customerService.save(customer);
        } catch (DuplicateKeyException e) {
            model.addAttribute("error", "Customer with email " + customer.getEmailId() + " already exists");
            model.addAttribute("customer", customer);
            return "admin/create/customerCreateForm";
        }
        return "redirect:/admin/customers";
    }

    @GetMapping("admin/customers/update")
    public String updateCustomer(@RequestParam String id, Model model) {
        model.addAttribute("customer", customerService.findById(id).get());
        return "admin/update/customerUpdateForm";
    }

    @PostMapping("admin/customers/update")
    public String updateCustomer(@ModelAttribute("customer") Customer customer, @RequestParam String id, Model model) {

        try {
            customerService.update(customer, id);
        } catch (DuplicateKeyException e) {
            model.addAttribute("error", "Customer with email " + customer.getEmailId() + " already exists");
            model.addAttribute("customer", customer);
            return "admin/update/customerUpdateForm";
        }
        
        return "redirect:/admin/customers";
    }

    @GetMapping("admin/customers/delete")
    public String deleteCustomer(@RequestParam String id) {
        customerService.delete(id);
        return "redirect:/admin/customers";
    }
    
    @GetMapping("admin/orders")
    public String showOrders(Model model) {
        List<CustomerOrder> customerOrders = customerOrderService.findAll();
        model.addAttribute("customerOrders", customerOrders);
        model.addAttribute("manufacturerOrders", manufacturerOrderService.findAll());
        Map<Long, String> customerOrderStatusMap = shippingInfoCustomerOrderService.idStatusMap();
        model.addAttribute("customerOrderStatusMap", customerOrderStatusMap);
        Map<Long, String> manufacturerOrderStatusMap = shippingInfoManufacturerOrderService.idStatusMap();
        model.addAttribute("manufacturerOrderStatusMap", manufacturerOrderStatusMap);
        return "admin/orders";
    }

    @GetMapping("admin/orders/customer/create")
    public String createCustomerOrder(Model model) {
        model.addAttribute("customerOrder", new CustomerOrder());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("employees", employeeService.findAll());
        return "admin/create/customerOrderCreateForm";
    }

    @PostMapping("admin/orders/customer/create")
    public String createCustomerOrder(@ModelAttribute("customerOrder") CustomerOrder customerOrder,
                                      @RequestParam("productIds") List<Long> productIds,
                                      @RequestParam("quantities") List<Integer> quantities,
                                      @RequestParam("customerId") String customerId,
                                      @RequestParam("employeeId") Long employeeId,
                                      Model model) {
        customerOrder.setCustomer(customerService.findById(customerId).get());
        customerOrder.setProcessorEmployee(employeeService.findById(employeeId).get());
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.findById(productIds.get(i)).get();
            customerOrder.getProducts().add(new CustomerOrder.Pair<>(product, quantities.get(i)));
        }

        for (CustomerOrder.Pair<Product, Integer> product : customerOrder.getProducts()) {
            if (product.getFirst().getStockQuantity() < product.getSecond()) {
                model.addAttribute("error", "Not enough stock for product " + product.getFirst().getProductName() + " (Available: " + product.getFirst().getStockQuantity() + ")");
                model.addAttribute("customerOrder", customerOrder);
                model.addAttribute("products", productService.findAll());
                model.addAttribute("customers", customerService.findAll());
                model.addAttribute("employees", employeeService.findAll());
                return "admin/create/customerOrderCreateForm";
            }
        }
        
        customerOrderService.save(customerOrder);
        return "redirect:/admin/orders";
    }

    @GetMapping("admin/orders/customer/update")
    public String updateCustomerOrder(@RequestParam Long id, Model model) {
        CustomerOrder customerOrder = customerOrderService.findById(id).get();
        model.addAttribute("customerOrder", customerOrder);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("employees", employeeService.findAll());
        return "admin/update/customerOrderUpdateForm";
    }

    @PostMapping("admin/orders/customer/update")
    public String updateCustomerOrder(@ModelAttribute("customerOrder") CustomerOrder customerOrder,
                                      @RequestParam("productIds") List<Long> productIds,
                                      @RequestParam("quantities") List<Integer> quantities,
                                      @RequestParam("customerId") String customerId,
                                      @RequestParam("employeeId") Long employeeId,
                                      @RequestParam Long id,
                                      Model model) {
        customerOrder.setCustomer(customerService.findById(customerId).get());
        customerOrder.setProcessorEmployee(employeeService.findById(employeeId).get());
        customerOrder.getProducts().clear();

        CustomerOrder prevCustomerOrder = customerOrderService.findById(id).get();

        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.findById(productIds.get(i)).get();
            customerOrder.getProducts().add(new CustomerOrder.Pair<>(product, quantities.get(i)));
        }
        
        // for each product in the order, check if the quantity is available by increasing stock by previous quantity ordered
        for (CustomerOrder.Pair<Product, Integer> product : customerOrder.getProducts()) {
            if (product.getFirst().getStockQuantity() + prevCustomerOrder.getQuantity(product.getFirst().getProductId()) < product.getSecond()) {
                model.addAttribute("error", "Not enough stock for product " + product.getFirst().getProductName() + " (More Available: " + product.getFirst().getStockQuantity() + ")");
                model.addAttribute("customerOrder", customerOrder);
                model.addAttribute("products", productService.findAll());
                model.addAttribute("customers", customerService.findAll());
                model.addAttribute("employees", employeeService.findAll());
                return "admin/update/customerOrderUpdateForm";
            }
            
        }

        customerOrderService.update(customerOrder, id);
        return "redirect:/admin/orders";
    }

    @GetMapping("admin/orders/customer/delete")
    public String deleteCustomerOrder(@RequestParam Long id) {
        customerOrderService.delete(id);
        return "redirect:/admin/orders";
    }

  

    @PostMapping("admin/orders/manufacturer/create")
    public String createManufacturerOrder(@ModelAttribute("manufacturerOrder") ManufacturerOrder manufacturerOrder,
                                         @RequestParam("productIds") List<Long> productIds,
                                         @RequestParam("quantities") List<Integer> quantities,
                                         @RequestParam("manufacturerId") Long manufacturerId,
                                         @RequestParam("employeeId") Long employeeId) {
        manufacturerOrder.setManufacturer(manufacturerService.findById(manufacturerId).get());
        manufacturerOrder.setProcessedByEmployee(employeeService.findById(employeeId).get());
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.findById(productIds.get(i)).get();
            manufacturerOrder.getProducts().add(new CustomerOrder.Pair<>(product, quantities.get(i)));
        }
        manufacturerOrderService.save(manufacturerOrder);
        return "redirect:/admin/orders";
    }

    @GetMapping("admin/orders/manufacturer/create")
    public String createManufacturerOrder(Model model, @RequestParam(required = false) Long productId) {
        ManufacturerOrder manufacturerOrder;
        if (productId != null) {
            Optional<Product> product = productService.findById(productId);
            if (product.isPresent()) {
                Manufacturer manufacturer = product.get().getManufacturers().iterator().next().getFirst();
                manufacturerOrder = new ManufacturerOrder();
                manufacturerOrder.setManufacturer(manufacturer);
                manufacturerOrder.setProducts(Set.of(new CustomerOrder.Pair<>(product.get(), 1)));
            } else {
                manufacturerOrder = new ManufacturerOrder();  // Fallback if product is not found
            }
        } else {
            manufacturerOrder = new ManufacturerOrder();  // Default behavior when productId is not provided
        }

        model.addAttribute("manufacturerOrder", manufacturerOrder);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("manufacturers", manufacturerService.findAll());
        model.addAttribute("employees", employeeService.findAll());
        return "admin/create/manufacturerOrderCreateForm";
    }


    @GetMapping("admin/orders/manufacturer/update")
    public String updateManufacturerOrder(@RequestParam Long id, Model model) {
        ManufacturerOrder manufacturerOrder = manufacturerOrderService.findById(id).get();
        model.addAttribute("manufacturerOrder", manufacturerOrder);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("manufacturers", manufacturerService.findAll());
        model.addAttribute("employees", employeeService.findAll());
        return "admin/update/manufacturerOrderUpdateForm";
    }

    @PostMapping("admin/orders/manufacturer/update")
    public String updateManufacturerOrder(@ModelAttribute("manufacturerOrder") ManufacturerOrder manufacturerOrder,
                                         @RequestParam("productIds") List<Long> productIds,
                                         @RequestParam("quantities") List<Integer> quantities,
                                         @RequestParam("manufacturerId") Long manufacturerId,
                                         @RequestParam("employeeId") Long employeeId,
                                         @RequestParam Long id) {
        manufacturerOrder.setManufacturer(manufacturerService.findById(manufacturerId).get());
        manufacturerOrder.setProcessedByEmployee(employeeService.findById(employeeId).get());
        manufacturerOrder.getProducts().clear();
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.findById(productIds.get(i)).get();
            manufacturerOrder.getProducts().add(new CustomerOrder.Pair<>(product, quantities.get(i)));
        }
        manufacturerOrderService.update(manufacturerOrder, id);
        return "redirect:/admin/orders";
    }

    @GetMapping("admin/orders/manufacturer/delete")
    public String deleteManufacturerOrder(@RequestParam Long id) {
        manufacturerOrderService.delete(id);
        return "redirect:/admin/orders";
    }

    @GetMapping("admin/orders/customer/shipping")
    public String showShippingInfoCustomerOrder(@RequestParam Long id, Model model) {
        model.addAttribute("customerOrder", customerOrderService.findById(id).get());
        model.addAttribute("shippingInfo", shippingInfoCustomerOrderService.findByOrderId(id).get());
        return "admin/customerShipping";
    }

    @PostMapping("admin/orders/customer/shipping/update")
    public String updateShippingInfoCustomerOrder(@ModelAttribute("shippingInfo") ShippingInfoCustomerOrder shippingInfo,
                                                @RequestParam("orderId") Long orderId,
                                                @RequestParam Long id) {
        shippingInfoCustomerOrderService.update(shippingInfo, id);
        CustomerOrder customerOrder = customerOrderService.findById(orderId).get();
        if (shippingInfo.getStatus().equals(ShippingInfoCustomerOrder.Status.CANCELLED))
        {
            if(orderReturnsService.findById(customerOrder).isPresent())
            {
                return "redirect:/admin/orders";
            }
            OrderReturns orderReturns = new OrderReturns();
                        orderReturns.setOrder(customerOrder);
                        orderReturns.setReturnDate(new java.sql.Date(System.currentTimeMillis()));
                        orderReturns.setReturnReason("Order Cancelled");
            orderReturnsService.save(orderReturns);
            for (CustomerOrder.Pair<Product, Integer> product : customerOrder.getProducts()) {
                productService.updateProductQuantity(product.getFirst().getProductId(), -product.getSecond());
            }
        }
        else{
            if(orderReturnsService.findById(customerOrder).isPresent())
            {
                for (CustomerOrder.Pair<Product, Integer> product : customerOrder.getProducts()) {
                    productService.updateProductQuantity(product.getFirst().getProductId(), product.getSecond());
                }
                orderReturnsService.delete(customerOrder);
            }
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("admin/orders/manufacturer/shipping")
    public String showShippingInfoManufacturerOrder(@RequestParam Long id, Model model) {
        model.addAttribute("manufacturerOrder", manufacturerOrderService.findById(id).get());
        model.addAttribute("shippingInfo", shippingInfoManufacturerOrderService.findByOrderId(id).get());
        return "admin/manufacturerShipping";
    }

    @PostMapping("admin/orders/manufacturer/shipping/update")
    public String updateShippingInfoManufacturerOrder(@ModelAttribute("shippingInfo") ShippingInfoManufacturerOrder shippingInfo,
                                                    @RequestParam("orderId") Long orderId,
                                                    @RequestParam Long id) {
                                                        ManufacturerOrder manufacturerOrder = manufacturerOrderService.findById(orderId).get();
        if (shippingInfo.getStatus().equals(ShippingInfoManufacturerOrder.Status.ARRIVED))
        {
            for (CustomerOrder.Pair<Product, Integer> product : manufacturerOrder.getProducts()) {
                productService.updateProductQuantity(product.getFirst().getProductId(), -product.getSecond());
            }
        }
        else{
            ShippingInfoManufacturerOrder shippingInfoManufacturerOrder = shippingInfoManufacturerOrderService.findById(id).get();
            if(shippingInfoManufacturerOrder.getStatus().equals(ShippingInfoManufacturerOrder.Status.ARRIVED))
            {
                for (CustomerOrder.Pair<Product, Integer> product : manufacturerOrder.getProducts()) {
                    productService.updateProductQuantity(product.getFirst().getProductId(), product.getSecond());
                }
            }
        }
        shippingInfoManufacturerOrderService.update(shippingInfo, id);
        return "redirect:/admin/orders";
    }


    @GetMapping("admin/orders/customer/return")
    public String showAllReturnedOrders(Model model) {
        List<OrderReturns> orderReturns = orderReturnsService.findAll();
        System.out.println(orderReturns);
        model.addAttribute("returns", orderReturns);
        return "admin/customerReturns";
    }

    @GetMapping("admin/sales/weekly")
    public ResponseEntity<Map<Date, Double>> weeklySales(@RequestParam String date) {
        Date startDate = Date.valueOf(date);
        Date endDate = Date.valueOf(startDate.toLocalDate().plusDays(7));
        Map<Date, Double> weeklySales = salesReportService.weeklySales(startDate, endDate);
        return ResponseEntity.ok(weeklySales);
    }



}
