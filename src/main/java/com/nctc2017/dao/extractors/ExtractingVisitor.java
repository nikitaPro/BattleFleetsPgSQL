package com.nctc2017.dao.extractors;

import java.math.BigInteger;
import java.util.Map;

public interface ExtractingVisitor<E> {
    
    E visit(BigInteger entityId, Map<String, String> papamMap);
}
