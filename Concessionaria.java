import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class Concessionaria {
    static final String DB_FILE = "concessionaria.db";
    
    // matrizes principais
    public static String Clientes[][] = new String[5][3];
    public static String Vendedores[][] = new String[5][5];
    public static String Produtos[][] = new String[5][4];
    public static String Vendas[][] = new String[5][7];
    public static String VendasItems[][] = new String[25][6];
    public static String Comissoes[][] = new String[25][6];
    public static String Salarios[][] = new String[5][4];

    public static void main(String[] args) {
        DataStore ds = DataStore.load(DB_FILE);
        Scanner sc = new Scanner(System.in);
        
        // carrega matrizes
        carregarMatrizes(ds);
        
        while (true) {
            // menu principal
            System.out.println("\n=== SISTEMA CONCESSIONÁRIA ===");
            System.out.println("1 – PRODUTOS");
            System.out.println("2 – CLIENTES");
            System.out.println("3 – VENDEDORES");
            System.out.println("4 – VENDAS");
            System.out.println("5 – RELATÓRIOS");
            System.out.println("0 – SAIR");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "1": menuProdutos(ds, sc); break;
                case "2": menuClientes(ds, sc); break;
                case "3": menuVendedores(ds, sc); break;
                case "4": menuVendas(ds, sc); break;
                case "5": menuRelatorios(ds, sc); break;
                case "0":
                    ds.save(DB_FILE);
                    salvarMatrizes(ds);
                    System.out.println("Dados salvos. Encerrando...");
                    sc.close();
                    return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    // menu de produtos
    static void menuProdutos(DataStore ds, Scanner sc) {
        while (true) {
            System.out.println("\n--- PRODUTOS ---");
            System.out.println("1 – INSERIR DADOS DO PRODUTO");
            System.out.println("2 – ALTERAR DADOS DO PRODUTO");
            System.out.println("3 – EXCLUIR DADOS DO PRODUTO");
            System.out.println("4 – IMPRIMIR FICHA DO PRODUTO");
            System.out.println("0 – RETORNAR AO MENU ANTERIOR");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();
            if (op.equals("1")) {
                System.out.print("Marca: "); String marca = sc.nextLine();
                System.out.print("Modelo: "); String modelo = sc.nextLine();
                System.out.print("Ano: "); int ano = readInt(sc);
                System.out.print("Placa: "); String placa = sc.nextLine();
                System.out.print("Preço de compra: "); double compra = readDouble(sc);
                System.out.print("Margem de lucro (%): "); double margem = readDouble(sc);
                System.out.print("Quantidade: "); int qtd = readInt(sc);
                
                // calculo do preco de venda
                double precoVenda = compra * (1 + margem/100);
                
                ds.addVehicle(new Vehicle(ds.nextVehicleId(), marca, modelo, ano, placa, precoVenda, compra, qtd, margem));
                ds.save(DB_FILE);
                System.out.println("Veículo adicionado! Preço de venda: R$ " + String.format("%.2f", precoVenda));
            } else if (op.equals("2")) {
                ds.listVehicles();
                System.out.print("ID do veículo para alterar: "); int id = readInt(sc);
                if (!podeAlterarProduto(ds, id)) {
                    System.out.println("Não pode alterar - existem vendas registradas para este produto!");
                } else {
                    Vehicle v = ds.getVehicle(id);
                    if (v == null) {
                        System.out.println("Veículo não encontrado!");
                    } else {
                        System.out.println("\nDados atuais:");
                        System.out.println(v);
                        System.out.println("\nDigite os novos dados (ENTER para manter atual):");
                        
                        System.out.print("Marca [" + v.brand + "]: "); 
                        String marca = sc.nextLine().trim();
                        if (!marca.isEmpty()) v.brand = marca;
                        
                        System.out.print("Modelo [" + v.model + "]: "); 
                        String modelo = sc.nextLine().trim();
                        if (!modelo.isEmpty()) v.model = modelo;
                        
                        System.out.print("Ano [" + v.year + "]: "); 
                        String anoStr = sc.nextLine().trim();
                        if (!anoStr.isEmpty()) v.year = Integer.parseInt(anoStr);
                        
                        System.out.print("Placa [" + v.plate + "]: "); 
                        String placa = sc.nextLine().trim();
                        if (!placa.isEmpty()) v.plate = placa;
                        
                        System.out.print("Preço de compra [" + String.format("%.2f", v.buyPrice) + "]: "); 
                        String compraStr = sc.nextLine().trim();
                        if (!compraStr.isEmpty()) v.buyPrice = Double.parseDouble(compraStr.replace(",", "."));
                        
                        System.out.print("Margem de lucro (%) [" + String.format("%.1f", v.margem) + "]: "); 
                        String margemStr = sc.nextLine().trim();
                        if (!margemStr.isEmpty()) {
                            v.margem = Double.parseDouble(margemStr.replace(",", "."));
                            // recalcula preco
                            v.price = v.buyPrice * (1 + v.margem/100);
                        }
                        
                        System.out.print("Quantidade [" + v.quantity + "]: "); 
                        String qtdStr = sc.nextLine().trim();
                        if (!qtdStr.isEmpty()) v.quantity = Integer.parseInt(qtdStr);
                        
                        ds.save(DB_FILE);
                        System.out.println("Veículo alterado com sucesso!");
                    }
                }
            } else if (op.equals("3")) {
                ds.listVehicles();
                System.out.print("ID do veículo para excluir: "); int id = readInt(sc);
                if (!podeExcluirProduto(ds, id)) {
                    System.out.println("Não pode excluir - existem vendas registradas para este produto!");
                } else {
                    if (ds.removeVehicle(id)) {
                        ds.save(DB_FILE);
                        System.out.println("Veículo excluído!");
                    } else {
                        System.out.println("Veículo não encontrado.");
                    }
                }
            } else if (op.equals("4")) {
                ds.listVehicles();
                System.out.print("ID do veículo para imprimir: "); int id = readInt(sc);
                Vehicle v = ds.getVehicle(id);
                if (v != null) {
                    System.out.println("\n--- FICHA DO PRODUTO ---");
                    System.out.println(v);
                    System.out.print("Imprimir na tela (S) ou impressora (N)? ");
                    String imprimir = sc.nextLine().trim();
                    if (imprimir.equalsIgnoreCase("S")) {
                        System.out.println("Impressao na tela realizada!");
                    }
                } else {
                    System.out.println("Veículo não encontrado.");
                }
            } else if (op.equals("0")) return;
            else System.out.println("Inválido.");
        }
    }

    // menu de clientes
    static void menuClientes(DataStore ds, Scanner sc) {
        while (true) {
            System.out.println("\n--- CLIENTES ---");
            System.out.println("1 – INSERIR DADOS DO CLIENTE");
            System.out.println("2 – ALTERAR DADOS DO CLIENTE");
            System.out.println("3 – EXCLUIR DADOS DO CLIENTE");
            System.out.println("4 – IMPRIMIR FICHA DO CLIENTE");
            System.out.println("0 – RETORNAR AO MENU ANTERIOR");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();
            if (op.equals("1")) {
                System.out.print("Nome: "); String nome = sc.nextLine();
                System.out.print("CPF: "); String cpf = sc.nextLine();
                System.out.print("Telefone: "); String tel = sc.nextLine();
                System.out.print("Email: "); String email = sc.nextLine();
                ds.addClient(new Client(ds.nextClientId(), nome, cpf, tel, email));
                ds.save(DB_FILE);
                System.out.println("Cliente cadastrado!");
            } else if (op.equals("2")) {
                ds.listClients();
                System.out.print("ID do cliente para alterar: "); int id = readInt(sc);
                if (!podeAlterarCliente(ds, id)) {
                    System.out.println("Não pode alterar - existem vendas registradas para este cliente!");
                } else {
                    Client c = ds.getClient(id);
                    if (c == null) {
                        System.out.println("Cliente não encontrado!");
                    } else {
                        System.out.println("\nDados atuais:");
                        System.out.println(c);
                        System.out.println("\nDigite os novos dados (ENTER para manter atual):");
                        
                        System.out.print("Nome [" + c.name + "]: "); 
                        String nome = sc.nextLine().trim();
                        if (!nome.isEmpty()) c.name = nome;
                        
                        System.out.print("CPF [" + c.cpf + "]: "); 
                        String cpf = sc.nextLine().trim();
                        if (!cpf.isEmpty()) c.cpf = cpf;
                        
                        System.out.print("Telefone [" + c.phone + "]: "); 
                        String tel = sc.nextLine().trim();
                        if (!tel.isEmpty()) c.phone = tel;
                        
                        System.out.print("Email [" + c.email + "]: "); 
                        String email = sc.nextLine().trim();
                        if (!email.isEmpty()) c.email = email;
                        
                        ds.save(DB_FILE);
                        System.out.println("Cliente alterado com sucesso!");
                    }
                }
            } else if (op.equals("3")) {
                ds.listClients();
                System.out.print("ID do cliente para excluir: "); int id = readInt(sc);
                if (!podeExcluirCliente(ds, id)) {
                    System.out.println("Não pode excluir - existem vendas registradas para este cliente!");
                } else {
                    if (ds.removeClient(id)) {
                        ds.save(DB_FILE);
                        System.out.println("Cliente excluído!");
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
                }
            } else if (op.equals("4")) {
                ds.listClients();
                System.out.print("ID do cliente para imprimir: "); int id = readInt(sc);
                Client c = ds.getClient(id);
                if (c != null) {
                    System.out.println("\n--- FICHA DO CLIENTE ---");
                    System.out.println(c);
                    System.out.print("Imprimir na tela (S) ou impressora (N)? ");
                    String imprimir = sc.nextLine().trim();
                    if (imprimir.equalsIgnoreCase("S")) {
                        System.out.println("Impressao na tela realizada!");
                    }
                } else {
                    System.out.println("Cliente não encontrado.");
                }
            } else if (op.equals("0")) return;
            else System.out.println("Inválido.");
        }
    }

    // menu de vendedores
    static void menuVendedores(DataStore ds, Scanner sc) {
        while (true) {
            System.out.println("\n--- VENDEDORES ---");
            System.out.println("1 – INSERIR DADOS DO VENDEDOR");
            System.out.println("2 – ALTERAR DADOS DO VENDEDOR");
            System.out.println("3 – EXCLUIR DADOS DO VENDEDOR");
            System.out.println("4 – IMPRIMIR FICHA DO VENDEDOR");
            System.out.println("0 – RETORNAR AO MENU ANTERIOR");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();
            if (op.equals("1")) {
                System.out.print("Nome: "); String nome = sc.nextLine();
                System.out.print("Matrícula: "); String mat = sc.nextLine();
                System.out.print("Salário base: "); double base = readDouble(sc);
                System.out.print("Comissão (%): "); double perc = readDouble(sc);
                ds.addSeller(new Seller(ds.nextSellerId(), nome, mat, base, perc));
                ds.save(DB_FILE);
                System.out.println("Vendedor cadastrado!");
            } else if (op.equals("2")) {
                ds.listSellers();
                System.out.print("ID do vendedor para alterar: "); int id = readInt(sc);
                if (!podeAlterarVendedor(ds, id)) {
                    System.out.println("Não pode alterar - existem vendas registradas para este vendedor!");
                } else {
                    Seller s = ds.getSeller(id);
                    if (s == null) {
                        System.out.println("Vendedor não encontrado!");
                    } else {
                        System.out.println("\nDados atuais:");
                        System.out.println(s);
                        System.out.println("\nDigite os novos dados (ENTER para manter atual):");
                        
                        System.out.print("Nome [" + s.name + "]: "); 
                        String nome = sc.nextLine().trim();
                        if (!nome.isEmpty()) s.name = nome;
                        
                        System.out.print("Matrícula [" + s.matricula + "]: "); 
                        String mat = sc.nextLine().trim();
                        if (!mat.isEmpty()) s.matricula = mat;
                        
                        System.out.print("Salário base [" + String.format("%.2f", s.baseSalary) + "]: "); 
                        String baseStr = sc.nextLine().trim();
                        if (!baseStr.isEmpty()) s.baseSalary = Double.parseDouble(baseStr.replace(",", "."));
                        
                        System.out.print("Comissão (%) [" + String.format("%.1f", s.commissionPercent) + "]: "); 
                        String percStr = sc.nextLine().trim();
                        if (!percStr.isEmpty()) s.commissionPercent = Double.parseDouble(percStr.replace(",", "."));
                        
                        ds.save(DB_FILE);
                        System.out.println("Vendedor alterado com sucesso!");
                    }
                }
            } else if (op.equals("3")) {
                ds.listSellers();
                System.out.print("ID do vendedor para excluir: "); int id = readInt(sc);
                if (!podeExcluirVendedor(ds, id)) {
                    System.out.println("Não pode excluir - existem vendas registradas para este vendedor!");
                } else {
                    if (ds.removeSeller(id)) {
                        ds.save(DB_FILE);
                        System.out.println("Vendedor excluído!");
                    } else {
                        System.out.println("Vendedor não encontrado.");
                    }
                }
            } else if (op.equals("4")) {
                ds.listSellers();
                System.out.print("ID do vendedor para imprimir: "); int id = readInt(sc);
                Seller s = ds.getSeller(id);
                if (s != null) {
                    System.out.println("\n--- FICHA DO VENDEDOR ---");
                    System.out.println(s);
                    System.out.print("Imprimir na tela (S) ou impressora (N)? ");
                    String imprimir = sc.nextLine().trim();
                    if (imprimir.equalsIgnoreCase("S")) {
                        System.out.println("Impressao na tela realizada!");
                    }
                } else {
                    System.out.println("Vendedor não encontrado.");
                }
            } else if (op.equals("0")) return;
            else System.out.println("Inválido.");
        }
    }

    // menu de vendas
    static void menuVendas(DataStore ds, Scanner sc) {
        while (true) {
            System.out.println("\n--- VENDAS ---");
            System.out.println("1 – INSERIR NOVA VENDA");
            System.out.println("2 – CANCELAR VENDA");
            System.out.println("3 – IMPRIMIR PEDIDO DE VENDA");
            System.out.println("0 – RETORNAR AO MENU ANTERIOR");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();
            if (op.equals("1")) registrarVenda(ds, sc);
            else if (op.equals("2")) cancelarVenda(ds, sc);
            else if (op.equals("3")) imprimirPedidoVenda(ds, sc);
            else if (op.equals("0")) return;
            else System.out.println("Inválido.");
        }
    }

    // menu de relatorios
    static void menuRelatorios(DataStore ds, Scanner sc) {
        while (true) {
            System.out.println("\n--- RELATÓRIOS ---");
            System.out.println("1 – RELAÇÃO DE PRODUTOS EM ESTOQUE");
            System.out.println("2 – RELAÇÃO DE CLIENTES CADASTRADOS");
            System.out.println("3 – RELAÇÃO DE VENDEDORES CADASTRADOS");
            System.out.println("4 – RELAÇÃO DOS SALÁRIOS LÍQUIDOS POR MÊS");
            System.out.println("5 – RELAÇÃO DAS VENDAS EFETUADAS POR MÊS");
            System.out.println("6 – REIMPRESSÃO OF PEDIDO DE VENDAS");
            System.out.println("0 – RETORNAR AO MENU ANTERIOR");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();
            if (op.equals("1")) ds.reportAvailableVehicles();
            else if (op.equals("2")) ds.listClients();
            else if (op.equals("3")) ds.listSellers();
            else if (op.equals("4")) ds.reportSalaries();
            else if (op.equals("5")) ds.listSales(true);
            else if (op.equals("6")) reimprimirPedido(ds, sc);
            else if (op.equals("0")) return;
            else System.out.println("Inválido.");
        }
    }

    // validacoes
    static boolean podeAlterarProduto(DataStore ds, int produtoId) {
        for (Sale sale : ds.sales.values()) {
            if (sale.getVehicleId() == produtoId && !sale.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    static boolean podeExcluirProduto(DataStore ds, int produtoId) {
        return podeAlterarProduto(ds, produtoId); // mesma regra
    }

    static boolean podeAlterarCliente(DataStore ds, int clienteId) {
        for (Sale sale : ds.sales.values()) {
            if (sale.getClientId() == clienteId && !sale.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    static boolean podeExcluirCliente(DataStore ds, int clienteId) {
        return podeAlterarCliente(ds, clienteId); // mesma regra
    }

    static boolean podeAlterarVendedor(DataStore ds, int vendedorId) {
        for (Sale sale : ds.sales.values()) {
            if (sale.getSellerId() == vendedorId && !sale.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    static boolean podeExcluirVendedor(DataStore ds, int vendedorId) {
        return podeAlterarVendedor(ds, vendedorId); // mesma regra
    }

    static void registrarVenda(DataStore ds, Scanner sc) {
        ds.listVehicles();
        System.out.print("ID do veículo: "); int vid = readInt(sc);
        Vehicle v = ds.getVehicle(vid);
        if (v == null) { System.out.println("Veículo inválido."); return; }
        if (v.getQuantity() <= 0) { System.out.println("Veículo sem estoque."); return; }

        ds.listClients();
        System.out.print("ID do cliente: "); int cid = readInt(sc);
        Client c = ds.getClient(cid);
        if (c == null) { System.out.println("Cliente inválido."); return; }

        ds.listSellers();
        System.out.print("ID do vendedor: "); int sid = readInt(sc);
        Seller s = ds.getSeller(sid);
        if (s == null) { System.out.println("Vendedor inválido."); return; }

        System.out.print("Quantidade vendida: "); int qtd = readInt(sc);
        if (qtd <= 0 || qtd > v.getQuantity()) { System.out.println("Quantidade inválida."); return; }

        double preco = v.getPrice();
        double comissao = preco * qtd * (s.getCommissionPercent() / 100.0);
        
        // cria a venda
        Sale sale = new Sale(ds.nextSaleId(), LocalDate.now(), vid, cid, sid, preco, qtd, comissao, s.getCommissionPercent());
        ds.addSale(sale);
        v.setQuantity(v.getQuantity() - qtd);
        ds.save(DB_FILE);
        System.out.println("Venda registrada! Comissão: R$ " + String.format("%.2f", comissao));
    }

    static void cancelarVenda(DataStore ds, Scanner sc) {
        ds.listSales(false);
        System.out.print("ID da venda: "); int id = readInt(sc);
        Sale sale = ds.getSale(id);
        if (sale == null) { System.out.println("Venda não encontrada."); return; }
        if (sale.isCancelled()) { System.out.println("Venda já cancelada."); return; }
        
        // verifica se ja foi impressa
        if (sale.isPrinted()) {
            System.out.println("Venda já foi impressa e não pode ser cancelada!");
            return;
        }
        
        sale.setCancelled(true);
        Vehicle v = ds.getVehicle(sale.getVehicleId());
        if (v != null) v.setQuantity(v.getQuantity() + sale.getQuantity());
        ds.save(DB_FILE);
        System.out.println("Venda cancelada e estoque restaurado.");
    }

    static void imprimirPedidoVenda(DataStore ds, Scanner sc) {
        ds.listSales(false);
        System.out.print("ID da venda para imprimir: "); int id = readInt(sc);
        imprimirPedidoVendaComID(ds, sc, id);
    }

    static void reimprimirPedido(DataStore ds, Scanner sc) {
        ds.listSales(false);
        System.out.print("ID da venda para reimprimir: "); int id = readInt(sc);
        Sale sale = ds.getSale(id);
        if (sale == null) { System.out.println("Venda não encontrada."); return; }
        if (sale.isCancelled()) { System.out.println("Venda cancelada - não pode reimprimir."); return; }
        
        // usa funcao auxiliar
        imprimirPedidoVendaComID(ds, sc, id);
    }

    // funcao auxiliar de impressao
    static void imprimirPedidoVendaComID(DataStore ds, Scanner sc, int id) {
        Sale sale = ds.getSale(id);
        if (sale == null) { System.out.println("Venda não encontrada."); return; }
        if (sale.isCancelled()) { System.out.println("Venda cancelada - não pode imprimir."); return; }
        
        System.out.println("\n=== PEDIDO DE VENDA ===");
        System.out.println("Número: " + sale.getId());
        System.out.println("Data: " + sale.getDate());
        System.out.println("Cliente: " + ds.getClient(sale.getClientId()).name);
        System.out.println("Vendedor: " + ds.getSeller(sale.getSellerId()).getName());
        System.out.println("Veículo: " + ds.getVehicle(sale.getVehicleId()).brand + " " + ds.getVehicle(sale.getVehicleId()).model);
        System.out.println("Quantidade: " + sale.getQuantity());
        System.out.println("Valor unitário: R$ " + String.format("%.2f", sale.getPrice()));
        System.out.println("Valor total: R$ " + String.format("%.2f", sale.getPrice() * sale.getQuantity()));
        System.out.println("========================");
        
        sale.setPrinted(true);
        ds.save(DB_FILE);
        System.out.println("Pedido impresso com sucesso!");
    }

    // manipulacao de matrizes
    static void carregarMatrizes(DataStore ds) {
        // clientes
        int i = 0;
        for (Client client : ds.clients.values()) {
            if (i < 5) {
                Clientes[i][0] = String.valueOf(client.id);
                Clientes[i][1] = client.name;
                Clientes[i][2] = client.phone;
                i++;
            }
        }
        
        // vendedores
        i = 0;
        for (Seller seller : ds.sellers.values()) {
            if (i < 5) {
                Vendedores[i][0] = String.valueOf(seller.id);
                Vendedores[i][1] = seller.name;
                Vendedores[i][2] = seller.matricula;
                Vendedores[i][3] = String.valueOf(seller.baseSalary);
                Vendedores[i][4] = String.valueOf(seller.commissionPercent);
                i++;
            }
        }
        
        // produtos
        i = 0;
        for (Vehicle vehicle : ds.vehicles.values()) {
            if (i < 5) {
                Produtos[i][0] = String.valueOf(vehicle.id);
                Produtos[i][1] = vehicle.brand + " " + vehicle.model;
                Produtos[i][2] = String.valueOf(vehicle.buyPrice);
                Produtos[i][3] = String.valueOf(vehicle.margem);
                i++;
            }
        }
    }

    static void salvarMatrizes(DataStore ds) {
        // matrizes em memoria
        System.out.println("Matrizes salvas em memoria");
    }

    static int readInt(Scanner sc) {
        while (true) {
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.print("Valor inválido, tente novamente: "); }
        }
    }

    static double readDouble(Scanner sc) {
        while (true) {
            try { return Double.parseDouble(sc.nextLine().trim().replace(",", ".")); }
            catch (Exception e) { System.out.print("Valor inválido, tente novamente: "); }
        }
    }

    // classes
    static class Client implements Serializable {
        private static final long serialVersionUID = 1L;
        int id; String name, cpf, phone, email;
        Client(int id, String name, String cpf, String phone, String email) {
            this.id = id; this.name = name; this.cpf = cpf; this.phone = phone; this.email = email;
        }
        public int getId() { return id; }
        public String toString() { return String.format("ID:%d | %s | CPF:%s | Tel:%s | Email:%s", id, name, cpf, phone, email); }
    }

    static class Seller implements Serializable {
        private static final long serialVersionUID = 1L;
        int id; String name, matricula; double baseSalary, commissionPercent;
        Seller(int id, String name, String matricula, double baseSalary, double commissionPercent) {
            this.id = id; this.name = name; this.matricula = matricula; this.baseSalary = baseSalary; this.commissionPercent = commissionPercent;
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public double getBaseSalary() { return baseSalary; }
        public double getCommissionPercent() { return commissionPercent; }
        public String toString() { 
            return String.format("ID:%d | %s | Mat:%s | Base:R$%.2f | Comissão:%.2f%%", 
                    id, name, matricula, baseSalary, commissionPercent); 
        }
    }

    static class Vehicle implements Serializable {
        private static final long serialVersionUID = 1L;
        int id; String brand, model, plate; int year; double price, buyPrice; int quantity; double margem;
        Vehicle(int id, String brand, String model, int year, String plate, double price, double buyPrice, int quantity, double margem) {
            this.id = id; this.brand = brand; this.model = model; this.year = year; this.plate = plate; 
            this.price = price; this.buyPrice = buyPrice; this.quantity = quantity; this.margem = margem;
        }
        public int getId() { return id; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int q) { this.quantity = q; }
        public String toString() { 
            return String.format("ID:%d | Marca:%s | Modelo:%s | Ano:%d | Placa:%s | Venda:R$%.2f | Compra:R$%.2f | Margem:%.1f%% | Qtd:%d", 
                    id, brand, model, year, plate, price, buyPrice, margem, quantity); 
        }
    }

    static class Sale implements Serializable {
        private static final long serialVersionUID = 1L;
        int id; LocalDate date; int vehicleId, clientId, sellerId; double price; int quantity; 
        double commission; double percentualComissao; boolean cancelled = false; boolean printed = false;
        
        Sale(int id, LocalDate date, int vehicleId, int clientId, int sellerId, double price, int quantity, double commission, double percentualComissao) {
            this.id = id; this.date = date; this.vehicleId = vehicleId; this.clientId = clientId; 
            this.sellerId = sellerId; this.price = price; this.quantity = quantity; 
            this.commission = commission; this.percentualComissao = percentualComissao;
        }
        
        public int getId() { return id; }
        public LocalDate getDate() { return date; }
        public int getVehicleId() { return vehicleId; }
        public int getClientId() { return clientId; }
        public int getSellerId() { return sellerId; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public double getCommission() { return commission; }
        public boolean isCancelled() { return cancelled; }
        public boolean isPrinted() { return printed; }
        public void setCancelled(boolean c) { cancelled = c; }
        public void setPrinted(boolean p) { printed = p; }
        
        public String toString() {
            return String.format("ID:%d | Data:%s | Veículo:%d | Cliente:%d | Vendedor:%d | Qtd:%d | Valor unit:R$%.2f | Com:R$%.2f | Cancelada:%s | Impressa:%s",
                    id, date.toString(), vehicleId, clientId, sellerId, quantity, price, commission, 
                    cancelled ? "SIM" : "NÃO", printed ? "SIM" : "NÃO");
        }
    }

    static class DataStore implements Serializable {
        private static final long serialVersionUID = 1L;
        Map<Integer, Client> clients = new HashMap<>();
        Map<Integer, Seller> sellers = new HashMap<>();
        Map<Integer, Vehicle> vehicles = new HashMap<>();
        Map<Integer, Sale> sales = new HashMap<>();
        int clientSeq = 1, sellerSeq = 1, vehicleSeq = 1, saleSeq = 1;

        int nextClientId() { return clientSeq++; }
        int nextSellerId() { return sellerSeq++; }
        int nextVehicleId() { return vehicleSeq++; }
        int nextSaleId() { return saleSeq++; }

        void addClient(Client c) { clients.put(c.getId(), c); }
        boolean removeClient(int id) { return clients.remove(id) != null; }
        Client getClient(int id) { return clients.get(id); }
        void listClients() {
            System.out.println("\n--- CLIENTES ---");
            if (clients.isEmpty()) System.out.println("Nenhum cliente cadastrado.");
            else clients.values().stream().sorted(Comparator.comparingInt(Client::getId)).forEach(System.out::println);
        }

        void addSeller(Seller s) { sellers.put(s.getId(), s); }
        boolean removeSeller(int id) { return sellers.remove(id) != null; }
        Seller getSeller(int id) { return sellers.get(id); }
        void listSellers() {
            System.out.println("\n--- VENDEDORES ---");
            if (sellers.isEmpty()) System.out.println("Nenhum vendedor cadastrado.");
            else sellers.values().stream().sorted(Comparator.comparingInt(Seller::getId)).forEach(System.out::println);
        }

        void addVehicle(Vehicle v) { vehicles.put(v.getId(), v); }
        boolean removeVehicle(int id) { return vehicles.remove(id) != null; }
        Vehicle getVehicle(int id) { return vehicles.get(id); }
        boolean updateVehicleQty(int id, int q) { Vehicle v = vehicles.get(id); if (v == null) return false; v.setQuantity(q); return true; }
        void listVehicles() {
            System.out.println("\n--- ESTOQUE ---");
            if (vehicles.isEmpty()) System.out.println("Nenhum veículo cadastrado.");
            else vehicles.values().stream().sorted(Comparator.comparingInt(Vehicle::getId)).forEach(System.out::println);
        }

        void addSale(Sale s) { sales.put(s.getId(), s); }
        Sale getSale(int id) { return sales.get(id); }
        void listSales(boolean showCancelled) {
            System.out.println("\n--- VENDAS ---");
            if (sales.isEmpty()) { System.out.println("Nenhuma venda registrada."); return; }
            sales.values().stream().sorted(Comparator.comparingInt(Sale::getId))
                 .filter(s -> showCancelled || !s.isCancelled())
                 .forEach(System.out::println);
        }

        void reportAvailableVehicles() {
            System.out.println("\n--- VEÍCULOS DISPONÍVEIS ---");
            vehicles.values().stream().filter(v -> v.getQuantity() > 0).sorted(Comparator.comparingInt(Vehicle::getId)).forEach(System.out::println);
        }

        void reportCommissions() {
            System.out.println("\n--- COMISSÕES POR VENDEDOR ---");
            Map<Integer, Double> totals = new HashMap<>();
            for (Sale s : sales.values()) if (!s.isCancelled()) totals.put(s.getSellerId(), totals.getOrDefault(s.getSellerId(), 0.0) + s.getCommission());
            if (sellers.isEmpty()) System.out.println("Nenhum vendedor cadastrado.");
            else sellers.values().stream().sorted(Comparator.comparingInt(Seller::getId)).forEach(v -> {
                double t = totals.getOrDefault(v.getId(), 0.0);
                System.out.println(String.format("Vendedor: %s (ID %d) - Comissão total: R$ %.2f", v.getName(), v.getId(), t));
            });
        }

        void reportSalaries() {
            System.out.println("\n--- SALÁRIOS LÍQUIDOS ---");
            Map<Integer, Double> totals = new HashMap<>();
            for (Sale s : sales.values()) if (!s.isCancelled()) totals.put(s.getSellerId(), totals.getOrDefault(s.getSellerId(), 0.0) + s.getCommission());
            if (sellers.isEmpty()) System.out.println("Nenhum vendedor cadastrado.");
            else sellers.values().stream().sorted(Comparator.comparingInt(Seller::getId)).forEach(v -> {
                double com = totals.getOrDefault(v.getId(), 0.0);
                double net = v.getBaseSalary() + com;
                System.out.println(String.format("Vendedor: %s (ID %d) - Base: R$ %.2f, Comissão: R$ %.2f, Líquido: R$ %.2f", v.getName(), v.getId(), v.getBaseSalary(), com, net));
            });
        }

        void save(String filename) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) { oos.writeObject(this); }
            catch (Exception e) { System.out.println("Erro ao salvar: " + e.getMessage()); }
        }

        static DataStore load(String filename) {
            File f = new File(filename);
            if (!f.exists()) return new DataStore();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
                Object obj = ois.readObject();
                if (obj instanceof DataStore) return (DataStore) obj;
            } catch (Exception e) { System.out.println("Erro ao carregar banco: " + e.getMessage()); }
            return new DataStore();
        }
    }
}