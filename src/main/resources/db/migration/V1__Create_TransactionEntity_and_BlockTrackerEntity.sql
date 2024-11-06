CREATE TABLE transaction_entity (
                                    id BIGSERIAL PRIMARY KEY,
                                    transaction_hash VARCHAR(255) NOT NULL,
                                    from_address VARCHAR(255),
                                    to_address VARCHAR(255),
                                    value NUMERIC,
                                    block_number NUMERIC
);

CREATE TABLE block_tracker_entity (
                                      id VARCHAR(255) PRIMARY KEY,
                                      block_number BIGINT
);

INSERT INTO block_tracker_entity (id, block_number) VALUES ('lastProcessedBlock', 0);
