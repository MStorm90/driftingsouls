CREATE OR REPLACE VIEW friendly_nebula_scan_ranges AS SELECT s.star_system, s.x, s.y, ur.target_id, CONVERT(s.sensors*sm.sensorrange*0.01*((1.0 * (n.type IS NULL)) + (0.5* (n.type IS NOT NULL))), unsigned) sensor_range
                                                      FROM user_relations ur
                                                               INNER JOIN ships s ON s.owner=ur.user_id
                                                               INNER JOIN ships_modules sm
                                                                          ON sm.id = s.modules
                                                               LEFT JOIN nebel n
                                                                         ON (n.star_system=s.star_system AND n.x = s.x AND n.y = s.y)
                                                      WHERE ur.status=2 AND COALESCE(n.type, 1) NOT IN (4,5,6) AND LOCATE('nebelscan', sm.flags)
                                                      GROUP BY s.star_system, s.x, s.y, ur.target_id