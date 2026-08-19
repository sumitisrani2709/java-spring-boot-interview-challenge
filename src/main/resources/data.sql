-- Seed drivers around central Delhi. The pickup point used throughout the README is
-- Connaught Place (28.6139, 77.2090). Distances below are great-circle km from it.
INSERT INTO driver (name, latitude, longitude, status, version) VALUES
  ('Asha',   28.6145, 77.2100, 'AVAILABLE', 0),  --  0.12 km  <- nearest AVAILABLE driver
  ('Bilal',  28.6200, 77.2200, 'AVAILABLE', 0),  --  1.27 km
  ('Chetan', 28.6142, 77.2092, 'BUSY',      0),  --  0.04 km  but BUSY, must be ignored
  ('Divya',  28.6140, 77.2091, 'OFFLINE',   0),  --  0.02 km  but OFFLINE, must be ignored
  ('Esha',   28.7300, 77.0700, 'AVAILABLE', 0),  -- 18.72 km  outside the 15 km radius
  ('Farhan', 28.5355, 77.3910, 'AVAILABLE', 0);  -- 19.80 km  outside the 15 km radius
