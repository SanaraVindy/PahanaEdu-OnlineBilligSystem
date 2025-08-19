package controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Item;
import service.ItemService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet for handling all item-related API requests.
 * It supports CRUD operations for items.
 */
@WebServlet("/api/items/*")
public class ItemController extends HttpServlet {

    private final ItemService itemService = new ItemService();
    private final Gson gson = new Gson();

    /**
     * Handles HTTP GET requests to retrieve item data.
     * Supports retrieving all items or searching for specific items.
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Handle requests for all items or search
            String description = request.getParameter("description");
            String categoryIDStr = request.getParameter("categoryID");
            String minPriceStr = request.getParameter("minPrice");
            String maxPriceStr = request.getParameter("maxPrice");

            Integer categoryID = null;
            try {
                if (categoryIDStr != null && !categoryIDStr.trim().isEmpty()) {
                    categoryID = Integer.parseInt(categoryIDStr);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid categoryID format.\"}");
                return;
            }
            
            BigDecimal minPrice = null;
            try {
                if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
                    minPrice = new BigDecimal(minPriceStr);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid minPrice format.\"}");
                return;
            }

            BigDecimal maxPrice = null;
            try {
                if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
                    maxPrice = new BigDecimal(maxPriceStr);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid maxPrice format.\"}");
                return;
            }

            List<Item> items;
            if ((description != null && !description.trim().isEmpty()) || (categoryID != null) || (minPrice != null) || (maxPrice != null)) {
                items = itemService.searchItems(description, categoryID, minPrice, maxPrice);
            } else {
                items = itemService.getAllItems();
            }
            out.print(gson.toJson(items));
        } else {
            // Handle requests for a single item by ID
            try {
                int itemID = Integer.parseInt(pathInfo.substring(1));
                Item item = itemService.getItemById(itemID);
                if (item != null) {
                    out.print(gson.toJson(item));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Item not found.\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid item ID format.\"}");
            }
        }
    }

    /**
     * Handles HTTP POST requests to add a new item.
     * @param req The HttpServletRequest object.
     * @param resp The HttpServletResponse object.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            Item item = gson.fromJson(req.getReader(), Item.class);

            if (item == null ||
                item.getDescription() == null || item.getDescription().trim().isEmpty() ||
                item.getIdentificationCode() == null || item.getIdentificationCode().trim().isEmpty() ||
                item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0 ||
                item.getQuantity() < 0 ||
                item.getCategoryID() <= 0) {
                
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"All fields are required and must have valid values.\"}");
                return;
            }

            boolean isAdded = itemService.addItem(item);
            if (isAdded) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\":\"Item added successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"Database insertion failed\"}");
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format\"}");
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Handles HTTP PUT requests to update an existing item.
     * @param req The HttpServletRequest object.
     * @param resp The HttpServletResponse object.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Item ID is required for update.\"}");
            return;
        }

        try {
            int itemID = Integer.parseInt(pathInfo.substring(1));
            Item item = gson.fromJson(req.getReader(), Item.class);
            item.setItemID(itemID);

            if (item == null ||
                item.getDescription() == null || item.getDescription().trim().isEmpty() ||
                item.getIdentificationCode() == null || item.getIdentificationCode().trim().isEmpty() ||
                item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0 ||
                item.getQuantity() < 0 ||
                item.getCategoryID() <= 0) {
                
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"All fields are required and must have valid values.\"}");
                return;
            }
            
            boolean isUpdated = itemService.updateItem(item);
            if (isUpdated) {
                out.print("{\"message\":\"Item updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Item not found or update failed\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid item ID format.\"}");
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Server error: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Handles HTTP DELETE requests to remove an item.
     * @param req The HttpServletRequest object.
     * @param resp The HttpServletResponse object.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Item ID is required for deletion.\"}");
            return;
        }

        try {
            int itemID = Integer.parseInt(pathInfo.substring(1));
            boolean isDeleted = itemService.deleteItem(itemID);
            if (isDeleted) {
                out.print("{\"message\":\"Item deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Item not found or deletion failed\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid item ID format.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Server error: " + e.getMessage() + "\"}");
        }
    }
}
