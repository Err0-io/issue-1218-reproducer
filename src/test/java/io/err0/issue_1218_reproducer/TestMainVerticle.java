package io.err0.issue_1218_reproducer;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.impl.future.FailedFuture;
import io.vertx.core.impl.future.SucceededFuture;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgConnection;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Consumer;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  final static Logger logger = LoggerFactory.getLogger(TestMainVerticle.class);

  static void beginQueryCommit(SqlConnection con, String sql, Tuple tuple, Consumer<RowSet<Row>> onSuccess, Consumer<Throwable> onError) {
    con.preparedQuery(sql).execute(tuple).onComplete(ar2 -> {
      if (ar2.succeeded()) {
        final RowSet<Row> rows = ar2.result();
        try {
          onSuccess.accept(rows);
        } catch (Throwable throwable) {
          logger.warn("beginQueryCommit", throwable);
          onError.accept(throwable);
        }
      } else {
        onError.accept(ar2.cause());
      }
    });
  }

  static void closeEndingSession(SqlConnection con, Consumer<Void> after) {

    if (null == con) {
      after.accept(null);
      return;
    }

    con.query("SELECT pg_advisory_unlock_all();").execute().onComplete(ar2 -> {
      if (!ar2.succeeded()) {
        logger.warn(ar2.cause());
      }
      con.close(ar3 -> {
        if (! ar3.succeeded()) {
          logger.warn(ar3.cause());
        }
        try { after.accept(null); } catch (Throwable t) { logger.warn("closeEndingSession", t); }
      });
    });
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5555)
      .setHost("localhost")
      .setDatabase("db")
      .setUser("postgres")
      .setPassword("password");

    PgConnection.connect(vertx, connectOptions).onComplete(ar1 -> {
      if (ar1.succeeded()) {
        SqlConnection con = ar1.result();
        Consumer<Throwable> onError = throwable -> {
          logger.warn("An error was encountered, good.", throwable);
          testContext.completeNow(); // success (an error, it is a success here)
        };
        // Change the SQL below adding "LIMIT 100" to see it work:
        beginQueryCommit(con, "SELECT * FROM error_number_latest INNER JOIN prj ON (error_number_latest.prj_uuid = prj.prj_uuid)", Tuple.tuple(), rows -> {
          logger.warn("Query completed successfully, good.");
          testContext.completeNow();
        }, onError);
      } else {
        logger.warn("Test failed, unable to get a database connection.", ar1.cause());
        testContext.failNow("Unable to test, unable to get database connection.");
      }
    });
  }
}
