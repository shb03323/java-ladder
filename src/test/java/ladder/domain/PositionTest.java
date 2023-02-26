package ladder.domain;

import ladder.domain.player.Position;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PositionTest {

    @Test
    void 왼쪽으로_이동_테스트() {
        Position position = Position.from(3);
        position.moveLeft();

        assertThat(position.getPosition()).isEqualTo(2);
    }
}
