package com.devsuperior.dscatalog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	// A consulta ao banco de dados é realizada com JPQL.
	// O JPQL já devolve um objeto (obj) no formato da classe que está sendo consultada (Product).
	// Para utilizar um filtro é necessário usar o ":" + exatamente o mesmo nome do parâmetro, neste caso ":category". 
	// A cláusula IN necessita de uma lista, e neste caso, a lista é exatamete o nome do objeto lista definido na classe Product.
	// A cláusula IN só funciona se for realizado um join com a tabela de onde serão lidos os registros filhos.

	@Query("SELECT DISTINCT obj "
			+ "FROM Product obj "
			+ "INNER JOIN obj.categories cats "
			+ "WHERE (COALESCE(:categories) IS NULL OR cats IN :categories) "
			+ "AND (:name = '' OR LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%')))")
	Page<Product> find(List<Category> categories, String name, Pageable pageable);
}
