# Proyecto-2-Algoritmos Fase 1
Visualización de grafos:
Para poder ver los grafos, debe seguir estas instrucciones:
- Descargar Neo4j desktop (https://neo4j.com/download/)
- Clonar este repositorio localmente
- Abrir Neo4j desktop
- Crear una nueva instancia local (Local instance)
- Ingresar datos de inicio (usuario y contraseña)
Usuario: neo4j
Contraseña: algoritmos1234
- Asegurarse que la base de datos este corriendo, de lo contrario presionar start
- En el botón de connect, presionar "Query"
- Después de esto, correr el main en vs.
- Luego, en Neo4j desktop, ingresar estos comandos en la parte derecha de la pantalla, a la par de el símbolo "$"
MATCH (n)-[r]->(m)
RETURN n, r, m
LIMIT 50

Luego de esto, se pueden visualizar los nodos