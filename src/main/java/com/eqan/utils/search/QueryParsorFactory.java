package com.eqan.utils.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class QueryParsorFactory {
	private QueryParser q;

	@Bean
	public QueryParser getQueryParser() {
		if (q == null) {
			q = new QueryParser("canonical name", new StandardAnalyzer());
		}

		return q;
	}

}
