
<h3 align="center">
  Pós Tech Fiap  Arquitetura de Software 
</h3>

<h4 align="center">
  Tech Challenge Lata Velha
</h4>



* Java 21
* Spring Boot 3.2.5

Instalar o Docker Desktop 
pra subir no terminal digitar:  docker ps
pra subir o Postgres digitar: docker compose -f docker/docker-compose.yml up -d
pra derrubar o docker e limpar digitar: docker compose -f docker/docker-compose.yml down -v

Configuração do banco:
Host: localhost
Port: 5432
Database: minha_base
Username: admin
Password: admin123

acessar o swagger: http://localhost:8080/swagger-ui.html

usuarios: admin, atendente e mecanico 
senha: 123456

apos o token gerado copiar ele sem aspas e colar no authorize do swagger 
 
