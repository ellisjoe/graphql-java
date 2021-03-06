package graphql;

import graphql.language.SourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * This little helper allows GraphQlErrors to implement
 * common things (hashcode/ equals ) and to specification more easily
 */
@SuppressWarnings("SimplifiableIfStatement")
@Internal
public class GraphqlErrorHelper {

    public static Map<String, Object> toSpecification(GraphQLError error) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("message", error.getMessage());
        if (error.getLocations() != null) {
            errorMap.put("locations", locations(error.getLocations()));
        }
        if (error.getPath() != null) {
            errorMap.put("path", error.getPath());
        }

        Map<String, Object> extensions = error.getExtensions();
        ErrorClassification errorClassification = error.getErrorType();
        //
        // we move the ErrorClassification into extensions which allows
        // downstream people to see them but still be spec compliant
        if (errorClassification != null) {
            if (extensions != null) {
                extensions = new LinkedHashMap<>(extensions);
            } else {
                extensions = new LinkedHashMap<>();
            }
            // put in the classification unless its already there
            if (!extensions.containsKey("classification")) {
                extensions.put("classification", errorClassification.toSpecification(error));
            }
        }

        if (extensions != null) {
            errorMap.put("extensions", extensions);
        }
        return errorMap;
    }

    public static Object locations(List<SourceLocation> locations) {
        return locations.stream().map(GraphqlErrorHelper::location).collect(toList());
    }

    public static Object location(SourceLocation location) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("line", location.getLine());
        map.put("column", location.getColumn());
        return map;
    }

    public static int hashCode(GraphQLError dis) {
        int result = dis.getMessage() != null ? dis.getMessage().hashCode() : 0;
        result = 31 * result + (dis.getLocations() != null ? dis.getLocations().hashCode() : 0);
        result = 31 * result + (dis.getPath() != null ? dis.getPath().hashCode() : 0);
        result = 31 * result + dis.getErrorType().hashCode();
        return result;
    }

    public static boolean equals(GraphQLError dis, Object o) {
        if (dis == o) {
            return true;
        }
        if (o == null || dis.getClass() != o.getClass()) return false;

        GraphQLError dat = (GraphQLError) o;

        if (!Objects.equals(dis.getMessage(), dat.getMessage())) {
            return false;
        }
        if (!Objects.equals(dis.getLocations(), dat.getLocations())) {
            return false;
        }
        if (!Objects.equals(dis.getPath(), dat.getPath())) {
            return false;
        }
        return dis.getErrorType() == dat.getErrorType();
    }
}
